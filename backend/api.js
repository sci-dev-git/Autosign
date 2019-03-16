/*
 *  Autosign (AP-scanning-based Attendance Signature System)
 *  Copyright (C) 2019, AutoSig team. <diyer175@hotmail.com>
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License(GPL)
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 */
 
 /*
 * Status codes
 */
const EOK = 0;                  /* operation has been succeeded. */
const EFAULT = -1;              /* operation has failed. */
const EMISSING_PARAMEETR = -2;  /* missing parameetrs. */
const EINVALID_PARAMEETR = -3;  /* invalid parameter. */
const EUSER_NOT_FOUND = -4;     /* user not found. */
const EUSER_EXISTING = -5;      /* user is existing. */
const EINVALID_PASSWD = -6;     /* password is invalid */

/*
 * MySQL database parameters
 */
const DATABASE_GLOBAL = "autosig_db";
const TABLE_TEACHERS = "user_teachers";
const TABLE_STUDENTS = "user_students";
const MYSQL_USERNAME = "root";
const MYSQL_PASSWD = "admin";

function packResult(code, body) {
  var response = {'code': code, 'body': body};
  return JSON.stringify(response);
}
function errorMsgSQL(err) {
  return err.code + '(' + err.errno + ') ' + err.sqlMessage;
}

/*
 * Establish the connection to MySQL database.
 */
var mysql = require('mysql');
var sqlClient = mysql.createConnection({ user: MYSQL_USERNAME, password: MYSQL_PASSWD });
sqlClient.connect();

/*
 * Construct a express framework for RESTTful APIs.
 */
const express = require('express');
var app = express();

var crypto = require('crypto');
var md5 = crypto.createHash('md5');
var rsa = require('node-rsa');
const rsa_bits = 512;

/**
 * Interface of GET register_teacher.
 * @param id  The ID of this teacher.
 * @param name Real name of this teacher.
 * @param token Identity token. i.e. User-Given password encoded by MD5 and RSA.
 * @param bssid BSSID of the device of teacher, can be altered later.
 */
app.get('/register_teacher', (req, res) => {
  /*
   * Validate parameters
   */
  if (typeof(req.query.id) == 'undefined' ||
      typeof(req.query.name) == 'undefined' ||
      typeof(req.query.token) == 'undefined' ||
      typeof(req.query.bssid) == 'undefined') {
    res.send(packResult(EMISSING_PARAMEETR, undefined));
    return;
  }
  /*
   * See if there is the same teacher exisiting in the global database.
   */
  sqlClient.query(`use ${DATABASE_GLOBAL};`);
  sqlClient.query(`SELECT * FROM ${TABLE_TEACHERS} WHERE id = '${req.query.id}';`,
    function select_callback(err, results, fields) {
      if (err) {
        res.send(packResult(EFAULT, {msg: errorMsgSQL(err)}));
        return;
      }
      if(results.length) {
        res.send(packResult(EUSER_EXISTING, undefined));
        return;
      }
      /*
       * Insert the information of this teacher to global database.
       */
      var key = new rsa({b: rsa_bits}); // create and export a key pair all at once
      key.setOptions({ encryptionScheme: 'pkcs1' });
      var dbEntry = [req.query.id,
                     req.query.name,
                     req.query.bssid,
                     req.query.token,
                     key.exportKey('private'),
                     key.exportKey('public')];
      sqlClient.query(`INSERT INTO ${TABLE_TEACHERS}(id,name,bssid,ssid,token,private_key,public_key) VALUES(?,?,?,'',?,?,?);`, dbEntry,
        function (err, result) {
          if(err) {
            res.send(packResult(EFAULT, {msg: errorMsgSQL(err)}));
            return;
          }
          res.send(packResult(EOK, undefined));
      });
    });
});

/**
 * Interface of GET query_public_key.
 * Query the seed used to encode the password, whichi is generated when the user was registered.
 * @param id    The ID of this teacher.
 * @param type  The type of user. 0 = teachers, 1 = students.
 */
app.get('/query_public_key', (req, res) => {
  /*
   * Validate parameters
   */
  if (typeof(req.query.id) == 'undefined' ||
      typeof(req.query.type) == 'undefined') {
    res.send(packResult(EMISSING_PARAMEETR, undefined));
    return;
  }
  var tables = [TABLE_TEACHERS, TABLE_STUDENTS];
  var tableIdx = parseInt(req.query.type);

  if(tableIdx >= 0 && tableIdx <= 1) {
    var tbl = tables[tableIdx];
    // search this user in database
    sqlClient.query(`use ${DATABASE_GLOBAL};`);
    sqlClient.query(`SELECT * FROM ${tbl} WHERE id = '${req.query.id}';`,
      function select_callback(err, results, fields) {
        if (err) {
          res.send(packResult(EFAULT, {msg: errorMsgSQL(err)}));
          return;
        }
        if (results.length == 0) {
          res.send(packResult(EUSER_NOT_FOUND, undefined));
          return;
        }
        res.send(packResult(EOK, {public_key: results[0].public_key}));
      });
  } else {
      res.send(packResult(EINVALID_PARAMEETR, undefined));
  }
});

/**
 * password authentication.
 * @param id        User ID.
 * @param table     Which database table stored the target user ?
 * @param token     Identity token. i.e. User-Given password encoded by MD5 and RSA.
 * @param callback  Closure function to be called when the auth is completed.
 *                  callback: function(code, auth) {}
 *                  code: status code.
 *                  auth: Is the authentication successful ? true/false.
 */
function tokenAuth(id, table, token, callback) {
  sqlClient.query(`use ${DATABASE_GLOBAL};`);
  sqlClient.query(`SELECT * FROM ${table} WHERE id = '${id}';`,
    function select_callback(err, results, fields) {
      if (err) {
        callback(EFAULT, false);
        return;
      }
      if (results.length) {
        try {
          // Create and import a key pair
          var key = new rsa({b: rsa_bits});
          key.setOptions({ encryptionScheme: 'pkcs1' });
          key.importKey(results[0].private_key, 'private');
          key.importKey(results[0].public_key, 'public');
          
          /*
           * Decode the token encoded by RSA then do authentication.
           */
          var givenPasswdMD5 = key.decrypt(token, 'utf8');
          var dbPasswdMD5 = results[0].token;

          callback(EOK, (givenPasswdMD5 == dbPasswdMD5));
          return;
        } catch(e) {
          console.info(e);
          callback(EFAULT, false);
          return;
        }
      }
      callback(EFAULT, false);
    });
}

/**
 * Interface of GET alter_bssid.
 * @param id    The ID of this teacher.
 * @param bssid New value of BSSID.
 * @param token Identity token. i.e. User-Given password encoded by MD5 and RSA.
 */
app.get('/alter_bssid', (req, res) => {
  /*
   * Validate parameters
   */
  if (typeof(req.query.id) == 'undefined' ||
      typeof(req.query.bssid) == 'undefined' ||
      typeof(req.query.token) == 'undefined') {
    res.send(packResult(EMISSING_PARAMEETR, undefined));
    return;
  }
  sqlClient.query(`use ${DATABASE_GLOBAL};`);
  // password authentication
  tokenAuth(req.query.id, TABLE_TEACHERS, req.query.token, function (code, auth) {
    if (auth) {
      // find the teacher and alter the SSID
      sqlClient.query(`UPDATE ${TABLE_TEACHERS} SET bssid='${req.query.bssid}' WHERE id = '${req.query.id}';`,
        function update_callback(err, results, fields) {
          if (err) {
            res.send(packResult(EFAULT, {msg: errorMsgSQL(err)}));
            return;
          }
          if (results.affectedRows == 0) {
            res.send(packResult(EUSER_NOT_FOUND, undefined));
            return;
          }
          res.send(packResult(EOK, undefined));
        });
    } else {
      res.send(packResult(EINVALID_PASSWD, undefined));
    }
  });
});

/**
 * Interface of GET alter_ssid.
 * @param id    The ID of this teacher.
 * @param ssid  New value of SSID
 * @param token Identity token. i.e. User-Given password encoded by MD5 and RSA.
 */
app.get('/alter_ssid', (req, res) => {
  /*
   * Validate parameters
   */
  if (typeof(req.query.id) == 'undefined' ||
      typeof(req.query.ssid) == 'undefined' ||
      typeof(req.query.token) == 'undefined') {
    res.send(packResult(EMISSING_PARAMEETR, undefined));
    return;
  }
  sqlClient.query(`use ${DATABASE_GLOBAL};`);
  // password authentication
  tokenAuth(req.query.id, TABLE_TEACHERS, req.query.token, function (code, auth) {
    if (auth) {
      // find the teacher and then alter the SSID
      sqlClient.query(`UPDATE ${TABLE_TEACHERS} SET ssid='${req.query.ssid}' WHERE id = '${req.query.id}';`,
        function update_callback(err, results, fields) {
          if (err) {
            res.send(packResult(EFAULT, {msg: errorMsgSQL(err)}));
            return;
          }
          if (results.affectedRows == 0) {
            res.send(packResult(EUSER_NOT_FOUND, undefined));
            return;
          }
          res.send(packResult(EOK, undefined));
        });
    } else {
      res.send(packResult(EINVALID_PASSWD, undefined));
    }
  });
});

/*
 * Process the arguments passed from command-line.
 */
argv = process.argv.splice(2);
if (argv.length) {
  if ('--rebuild-db' == argv[0]) {
    sqlClient.query(`USE ${DATABASE_GLOBAL};`, function (err, results, fields) {
      sqlClient.query(`DROP DATABASE ${DATABASE_GLOBAL};`, function(err, results, fields) {
        sqlClient.query(`CREATE DATABASE ${DATABASE_GLOBAL};`, function (err, results, fields) {
          if (!err) {
            sqlClient.query(`USE ${DATABASE_GLOBAL};`, function (err, results, fields) {
              if (!err) {
                sqlClient.query(`CREATE TABLE ${TABLE_TEACHERS} (
                                          id text not null,
                                          name text not null,
                                          bssid varchar(18) not null,
                                          ssid text not null,
                                          token text not null,
                                          private_key text not null,
                                          public_key text not null);`);
              } else throw err;
            });
          } else throw err;
        });
      });
    });
    
    
  }
}

var server = app.listen(8080, () => {
  var hostname = server.address().address;
  var port = server.address().port;
  console.log(`Server running at http://${hostname}:${port}/`);
});

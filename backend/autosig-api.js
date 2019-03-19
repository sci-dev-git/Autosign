/**@file
 * Service of all the APIs of Autosig.
 */

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
 Import modules
 */
const mysql       = require('mysql');
const express     = require('express');
const crypto      = require('crypto');
const request     = require('request');
const config      = require('./autosig-config');
const util        = require('./autosig-util');
const wxsupports  = require('./wx-supports');

var sqlClient = mysql.createConnection({ user: config.MYSQL_USERNAME, password: config.MYSQL_PASSWD });
var app = express();
var md5 = crypto.createHash('md5');
var rsa = require('node-rsa');
const rsa_bits = 512;


function packResult(code, body) {
  var response = {'code': code, 'body': body};
  return JSON.stringify(response);
}
function errorMsgSQL(err) {
  return err.code + '(' + err.errno + ') ' + err.sqlMessage;
}

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
    res.send(packResult(config.EMISSING_PARAMEETR, undefined));
    return;
  }
  /*
   * See if there is the same teacher exisiting in the global database.
   */
  sqlClient.query(`use ${config.DATABASE_GLOBAL};`);
  sqlClient.query(`SELECT * FROM ${config.TABLE_USER_TEACHERS} WHERE id = '${req.query.id}';`,
    function select_callback(err, results, fields) {
      if (err) {
        res.send(packResult(config.EFAULT, {msg: errorMsgSQL(err)}));
        return;
      }
      if(results.length) {
        res.send(packResult(config.EUSER_EXISTING, undefined));
        return;
      }
      /*
       * Insert the information of this teacher to global database.
       */
      var key = new rsa({b: rsa_bits}); // create and export a key pair all at once
      key.setOptions({ encryptionScheme: 'pkcs1' });
      var dbEntry = [req.query.id, req.query.name, req.query.bssid, req.query.token,
                     key.exportKey('private'),
                     key.exportKey('public')];
      sqlClient.query(`INSERT INTO ${config.TABLE_USER_TEACHERS}(id,name,bssid,ssid,token,private_key,public_key) VALUES(?,?,?,'',?,?,?);`, dbEntry,
        function (err, result) {
          if(err) {
            res.send(packResult(config.EFAULT, {msg: errorMsgSQL(err)}));
            return;
          }
          res.send(packResult(config.EOK, undefined));
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
    res.send(packResult(config.EMISSING_PARAMEETR, undefined));
    return;
  }
  var tables = [config.TABLE_USER_TEACHERS, config.TABLE_SUMMARY_STUDENTS];
  var tableIdx = parseInt(req.query.type);

  if(tableIdx >= 0 && tableIdx <= 1) {
    var tbl = tables[tableIdx];
    // search this user in database
    sqlClient.query(`use ${config.DATABASE_GLOBAL};`);
    sqlClient.query(`SELECT * FROM ${tbl} WHERE id = '${req.query.id}';`,
      function select_callback(err, results, fields) {
        if (err) {
          res.send(packResult(config.EFAULT, {msg: errorMsgSQL(err)}));
          return;
        }
        if (results.length == 0) {
          res.send(packResult(config.EUSER_NOT_FOUND, undefined));
          return;
        }
        res.send(packResult(config.EOK, {public_key: results[0].public_key}));
      });
  } else {
      res.send(packResult(config.EINVALID_PARAMEETR, undefined));
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
  sqlClient.query(`use ${config.DATABASE_GLOBAL};`);
  sqlClient.query(`SELECT * FROM ${table} WHERE id = '${id}';`,
    function select_callback(err, results, fields) {
      if (err) {
        callback(config.EFAULT, false);
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

          callback(config.EOK, (givenPasswdMD5 == dbPasswdMD5));
        } catch(e) {
          callback(config.EFAULT, false);
          return;
        }
        return;
      }
      callback(config.EFAULT, false);
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
    res.send(packResult(config.EMISSING_PARAMEETR, undefined));
    return;
  }
  sqlClient.query(`use ${config.DATABASE_GLOBAL};`);
  // password authentication
  tokenAuth(req.query.id, config.TABLE_USER_TEACHERS, req.query.token, function (code, auth) {
    if (auth) {
      // find the teacher and alter the SSID
      sqlClient.query(`UPDATE ${config.TABLE_USER_TEACHERS} SET bssid='${req.query.bssid}' WHERE id = '${req.query.id}';`,
        function update_callback(err, results, fields) {
          if (err) {
            res.send(packResult(config.EFAULT, {msg: errorMsgSQL(err)}));
            return;
          }
          if (results.affectedRows == 0) {
            res.send(packResult(config.EUSER_NOT_FOUND, undefined));
            return;
          }
          res.send(packResult(config.EOK, undefined));
        });
    } else {
      res.send(packResult(config.EINVALID_PASSWD, undefined));
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
    res.send(packResult(config.EMISSING_PARAMEETR, undefined));
    return;
  }
  sqlClient.query(`use ${config.DATABASE_GLOBAL};`);
  // password authentication
  tokenAuth(req.query.id, config.TABLE_USER_TEACHERS, req.query.token, function (code, auth) {
    if (auth) {
      // find the teacher and then alter the SSID
      sqlClient.query(`UPDATE ${config.TABLE_USER_TEACHERS} SET ssid='${req.query.ssid}' WHERE id = '${req.query.id}';`,
        function update_callback(err, results, fields) {
          if (err) {
            res.send(packResult(config.EFAULT, {msg: errorMsgSQL(err)}));
            return;
          }
          if (results.affectedRows == 0) {
            res.send(packResult(config.EUSER_NOT_FOUND, undefined));
            return;
          }
          res.send(packResult(config.EOK, undefined));
        });
    } else {
      res.send(packResult(config.EINVALID_PASSWD, undefined));
    }
  });
});


/*
 * Connect to the database and startup server
 */
sqlClient.connect(function(err, result) {
  if(err) {
    console.error(`Error: failed to connect to MySQL server @${MYSQL_PASSWD}.`);
    throw err;
  }
  
  /*
   * Process the arguments passed from command-line.
   */
  argv = process.argv.splice(2);
  if (argv.length) {
    if ('--rebuild-db' == argv[0]) {
      sqlClient.query(`USE ${config.DATABASE_GLOBAL};`, function (err, results, fields) {
        sqlClient.query(`DROP DATABASE ${config.DATABASE_GLOBAL};`, function(err, results, fields) {
          sqlClient.query(`CREATE DATABASE ${config.DATABASE_GLOBAL};`, function (err, results, fields) {
            if (!err) {
              sqlClient.query(`USE ${config.DATABASE_GLOBAL};`, function (err, results, fields) {
                if (!err) {
                  /** Create a database table for all teachers (manager users).
                    * @field id     ID of teacher user.
                    * @field name   Real name of this teacher.
                    * @field bssid  BSSID of AP device used by the teacher.
                    * @field ssid   SSID of AP device used by the teacher.
                    * @field token  MD5 Encoded password. Used for identity verification.
                    * @field private_key Private key of RSA to decode the MD5 password.
                    * @field public_key  Public key of RSA to encode the MD5 password.
                    */
                  sqlClient.query(`CREATE TABLE ${config.TABLE_USER_TEACHERS} (
                                            id text NOT NULL,
                                            name text NOT NULL,
                                            bssid varchar(18) NOT NULL,
                                            ssid text NOT NULL,
                                            token text NOT NULL,
                                            private_key text NOT NULL,
                                            public_key text NOT NULL);`);
                  /** Create a database table for summary table of all the students. This table has been divided into two parts:
                    * Part I: Stores the basic information of students, witch is imported by teachers and non modifiable for students.
                    * Part II: Store the account information that is registered by student. To tell whether a student is registered,
                    * simply see whether the value of field `id` is NULL.
                    * @field gid    Group ID, which identifies a group of students who takes the same courses.
                    * @field name   Real name of this student.
                    * @field cert_type Certificate type. 0 = ID Card; 1 = Student Card.
                    * @field cert_id  Certificate ID for identity verification.
                    * @field faculty Name of the faculty to which the student belongs.
                    * @field class  The class to which the student belongs.
                    * @field id     Registered ID in this system. NULL if not registered.
                    * @field wx_openid WeChat openid to bind the user.
                    * @field token  MD5 Encoded password. Used for identity verification.
                    * @field private_key Private key of RSA to decode the MD5 password.
                    * @field public_key  Public key of RSA to encode the MD5 password.
                    */
                  sqlClient.query(`CREATE TABLE ${config.TABLE_SUMMARY_STUDENTS} (
                                            gid int(32) NOT NULL AUTO_INCREMENT,
                                            name text NOT NULL,
                                            cert_type tinyint NOT NULL,
                                            cert_id text NOT NULL,
                                            faculty text NOT NULL,
                                            class text NOT NULL,
                                            id text DEFAULT NULL,
                                            wx_openid text NOT NULL,
                                            token text NOT NULL,
                                            private_key text NOT NULL,
                                            public_key text NOT NULL
                                            );`);
                  /** Create a table for courses
                   * @field cid   Unique, course ID. 
                   * @field name  Name of this course.
                   * @field teacher Teacher of this course.
                   * @field classrom Where to attend the course.
                   * @field timerule The code of rule to specify when should take the course.
                   */
                  sqlClient.query(`CREATE TABLE ${config.TABLE_COURSES} (
                                            cid int(32) NOT NULL AUTO_INCREMENT,
                                            name text NOT NULL,
                                            teacher int NOT NULL,
                                            classrom text NOT NULL,
                                            timerule text NOT NULL);`);
                  /** create a table for course selections
                    * @field gid    Group ID, which identifies a group of students who takes the same courses.
                    * @field cid    Course ID.
                    */
                  sqlClient.query(`CREATE TABLE ${config.TABLE_COURSE_SELECTION} (
                                            gid int(32) NOT NULL AUTO_INCREMENT,
                                            cid int(32) NOT NULL);`);
                  // create signature statist table
                  sqlClient.query(`CREATE TABLE ${config.TABLE_SIG_STATIS} (
                                            sigid int(32) NOT NULL AUTO_INCREMENT);`);
                } else throw err;
              });
            } else throw err;
          });
        });
      });
    } /* --rebuild-db */ else {
      console.warn(`Warning: Unknown parameter: "${argv[0]}", ignored.`);
    }
  }
  
  var server = app.listen(8080, () => {
    var hostname = server.address().address;
    var port = server.address().port;
    console.log(`Server running at http://${hostname}:${port}/`);
  });
});

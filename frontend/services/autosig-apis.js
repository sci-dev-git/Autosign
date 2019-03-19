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

const app = getApp()

const apiHost = "http://localhost:8080";

module.exports.queryBindStatus = function (jscode, callback) {
  wx.request({
    url: `${apiHost}/query_bind_status`,
    data: {
      jscode: jscode
    },
    success: function (res) {
      var responseObj = res.data;
      if (responseObj.errcode)
        callback(responseObj.errcode, null);
      else
        callback(responseObj.errcode, responseObj.body.status);
    },
    fail: function (res) {
      callback(app.EFAULT, null);
    }
  });
}

module.exports.queryPublicKey = function(id, type, callback) {
  wx.request({
    url: `${apiHost}/query_public_key`,
    data: {
      id: id,
      type: type
    },
    success: function (res) {
      var responseObj = res.data;
      if (responseObj.errcode)
        callback(responseObj.errcode, null);
      else
        callback(responseObj.errcode, responseObj.body.public_key);
    },
    fail: function (res) {
      callback(app.EFAULT, null);
    }
  });
}

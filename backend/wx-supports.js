/**@file
 * Support WeChat APIs
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

/**
 * Get the session key and openid of WeChat users.
 * See https://developers.weixin.qq.com/miniprogram/dev/api-backend/code2Session.html for reference.
 * @param appid Acquired from WeChat application.
 * @param secret AppSecret that is acquired from WeChat.
 * @param jscode The code acquired by wx.login() interface.
 * @param closure function(res) to receive the result: {errcode: x, openid: y, session_key: z}
 */
function code2Session(appid, secret, jscode, callback) {
  var options = {
    url: `https://api.weixin.qq.com/sns/jscode2session?appid=${appid}&secret=${secret}&js_code=${jscode}&grant_type=authorization_code`
  };

  request.get(options, function(err, response, body) {
    if (!err) {
      var resp = JSON.parse(response.body);
      if (!resp.errcode) {
        callback({errcode: 0, openid: resp.openid, session_key: resp.session_key});
        return;
      }
    }
    callback({errcode: -1});
  });
}

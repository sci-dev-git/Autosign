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

//app.js
App({
  EFAULT: -1,
  ELOGIN: -2,
  ESERVER_ABNORMAL: -3,

  /**
   * Utility function to report a error to user.
   * @param errcode Specify error code.
   */
  reportError: function (errcode) {
    var msg;
    switch (errcode) {
      case this.ELOGIN:
        msg = '登陆失败'; break;
      case this.ESERVER_ABNORMAL:
        msg = '服务器异常'; break;
      default:
        msg = '操作失败';
    }
    wx.showToast({
      title: msg + '，请稍后重试。',
      icon: 'none'
    });
  },

  onLaunch: function () {
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || [];
    logs.unshift(Date.now());

    wx.showLoading({
      title: '正在登陆中',
    })

    // 登录
    wx.login({
      success: res => {
        // 发送 res.code台换取 openId, sessionKey, unionId
        this.globalData.code = res.code;

        // 获取用户信息
        wx.getSetting({
          success: res => {
            if (res.authSetting['scope.userInfo']) {
              // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
              wx.getUserInfo({
                success: res => {
                  // 可以将 res 发送给后台解码出 unionId
                  this.globalData.userInfo = res.userInfo;
                  wx.hideLoading();

                  // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
                  // 所以此处加入 callback 以防止这种情况
                  if (this.userInfoReadyCallback) {
                    this.userInfoReadyCallback(res);
                  }
                },
                fail: res => {
                  wx.hideLoading();
                  app.reportError(app.ELOGIN);
                }
              })
            } else {
              wx.hideLoading();
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback({ userInfo: null });
              }
            }
          }
        })
      },
      fail: res => {
        wx.hideLoading();
        app.reportError(app.ELOGIN);
      }
    })
  },
  globalData: {
    userInfo: null,
    code: null
  }
})
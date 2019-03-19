//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    showUserInfo: false, // asuring the userinfo page is displayed after we received the data from getUserInfo()
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  //事件处理函数
  bindViewTap: function() {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  
  /*
   * These messes is about to deal with asynchronous acquaintance of user info :(
   */
  onLoad: function () {
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true,
        showUserInfo: true
      })
      this.startup();
    } else if (this.data.canIUse) {
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        var userInfoValid = (res.userInfo != null); // check if the user info has been acquired
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: userInfoValid,
          showUserInfo: true
        })
        if (userInfoValid) {
          this.startup();
        }
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true,
            showUserInfo: true
          })
          this.startup();
        },
        fail: res => {
          app.reportError(app.ELOGIN)
        }
      })
    }
  },
  getUserInfo: function(e) {
    console.log(e)
    app.globalData.userInfo = e.detail.detail.userInfo
    this.setData({
      userInfo: app.globalData.userInfo,
      hasUserInfo: true,
      showUserInfo: true
    })
    this.startup();
  },

  /**
   * Finish all the messes of login and navigate to home page !!!
   */
  startup: function() {
    // at first chekcing whether the account is binded.
    var autosig_api = require('../../services/autosig-apis');
    autosig_api.queryBindStatus(app.globalData.code, function (errcode, binded) {
      if (errcode) {
        app.reportError(app.ESERVER_ABNORMAL);
        return;
      }
      if (!binded) {
        wx./*navigateTo*/redirectTo({
          url: '../binduser/binduser',
        });
      } else {
        wx.redirectTo({
          url: '../homepage/homepage',
        });
      }
    });
  }
})

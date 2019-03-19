// pages/binduser/binduser.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userNameRules: {
      maxLength: {
        value: 6,
        message: '姓名最多6个字',
      },
      minLength: {
        value: 3,
        message: '姓名最少三个字',
      },
      required: {
        value: true,
        message: '必填',
      },
    },
    isRequired: {
      required: {
        value: true,
        message: '必填',
      },
    }
  },

  onLoad: function (options) {

  },

  onBindFormSubmit: function (e) {

  },

  onBindFormReset: function (e) {

  }

})

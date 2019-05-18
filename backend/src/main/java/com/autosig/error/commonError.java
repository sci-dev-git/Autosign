/** @file
 * This file processes response of all the common errors.
 */
/*
 *  Autosig (Backend server for autosig management program in WeChat-App)
 *  Copyright (C) 2019, TYUT-404 team. Developer <diyer175@hotmail.com>.
 *
 *  THIS PROJECT IS FREE SOFTWARE; YOU CAN REDISTRIBUTE IT AND/OR
 *  MODIFY IT UNDER THE TERMS OF THE GNU LESSER GENERAL PUBLIC LICENSE(GPL)
 *  AS PUBLISHED BY THE FREE SOFTWARE FOUNDATION; EITHER VERSION 2.1
 *  OF THE LICENSE, OR (AT YOUR OPTION) ANY LATER VERSION.
 *
 *  THIS PROJECT IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL,
 *  BUT WITHOUT ANY WARRANTY; WITHOUT EVEN THE IMPLIED WARRANTY OF
 *  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  SEE THE GNU
 *  LESSER GENERAL PUBLIC LICENSE FOR MORE DETAILS.
 */
package com.autosig.error;

import com.alibaba.fastjson.JSONObject;

public enum commonError {
    E_OK,
    E_FAULT,
    E_SERVER_FAULT,
    E_TOKEN_AUTH,
    E_USER_EXISTING,
    E_USER_NON_EXISTING,
    E_PERMISSION_DENIED,
    E_GROUP_EXISTING,
    E_GROUP_NON_EXISTING,
    E_ACTIVITY_EXISTING,
    E_ACTIVITY_NON_EXISTING,
    E_TASK_EXISTING,
    E_TASK_NON_EXISTING,
    E_ASSET_NOT_FOUND;

    /**
     * Pack this error to mapping.
     * @return JSON object contains the information of this status.
     *  field:  status : Status code in integer.
     *      status_msg : Status text in detail.
     */
    public JSONObject packageError() {
        JSONObject result = new JSONObject();
    
        result.put("code", this.ordinal());
        result.put("msg", this.name());
        return result;
    }
    
    /**
     * To indicate whether the code represents a succeeded operation
     * @return boolean True if succeeded.
     */
    public boolean succeeded() {
        return (this.ordinal() == E_OK.ordinal());
    }
    public boolean failed() {
        return !this.succeeded();
    }
}

/** @file
 * User domain class, which keeps all the basic information of the user.
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
package com.autosig.domain;

import java.util.List;
import java.util.LinkedList;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import com.alibaba.fastjson.JSONObject;
import com.autosig.error.commonError;

@Document(collection = "users")
public class UserBase {
    /** identify, fixed after registration */
    @Id
    private String openId;
    /** Type of this user, fixed after registration */
    private UserType type;
    /** The secondary ID code of this user */
    private String code;
    /** Real name */
    private String realName;
    /** Encoded password */
    private String password;
    /** list of groups ID references, only for USER_MANAGER */
    public List<String> groups;
    
    public UserBase() {
        this.groups = new LinkedList<String>();
    }
    
    /*
     * Getter/Setter
     */
    public String getOpenId() {
        return openId;
    }
    public String getCode() {
        return code;
    }
    public String getRealName() {
        return realName;
    }
    public List<String> getGroups() {
        return groups;
    }
    public String getPassword() {
        return password;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setOpenId(String openId) {
        this.openId = openId;
    }
    public UserType getType() {
        return type;
    }
    public void setType(UserType type) {
        this.type = type;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    /*
     * Operations on lists
     */
    public commonError addGroup(String groupId) {
        if (groups.contains(groupId)) {
            return commonError.E_GROUP_EXISTING;
        }
        groups.add(groupId);
        return commonError.E_OK;
    }
    public commonError removeGroup(String groupId) {
        if (!groups.contains(groupId)) {
            return commonError.E_GROUP_NON_EXISTING;
        }
        groups.remove(groupId);
        return commonError.E_OK;
    }
    
    /**
     * Get the basic information of this user, displaying in public,
     * @return
     */
    public JSONObject getBasicInfo() {
        JSONObject userInfo = new JSONObject();
        userInfo.put("openId", openId);
        userInfo.put("realName", realName);
        
        return userInfo;
    }
}

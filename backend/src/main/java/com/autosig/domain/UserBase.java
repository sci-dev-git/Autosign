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
    /** The secondary ID code of this user */
    private String code;
    /** Real name */
    private String realName;
    /** User place */
    private String place;
    /** list of created group uid */
    public List<String> createdGroups;
    /** list of attended group uid */
    public List<String> attendedGroups;

    public UserBase() {
        this.createdGroups = new LinkedList<String>();
        this.attendedGroups = new LinkedList<String>();
    }
    
    @Override
    public boolean equals(Object src) {
        return openId.compareTo(((UserBase)src).openId) == 0;
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
    public String getPlace() {
        return place;
    }
    public List<String> getCreatedGroups() {
        return createdGroups;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setPlace(String place) {
        this.place = code;
    }
    public void setOpenId(String openId) {
        this.openId = openId;
    }
    
    /*
     * Operations on lists
     */
    public commonError addCreatedGroup(String groupId) {
        if (createdGroups.contains(groupId)) {
            return commonError.E_GROUP_EXISTING;
        }
        createdGroups.add(groupId);
        return commonError.E_OK;
    }
    public commonError removeCreatedGroup(String groupId) {
        if (!createdGroups.contains(groupId)) {
            return commonError.E_GROUP_NON_EXISTING;
        }
        createdGroups.remove(groupId);
        return commonError.E_OK;
    }
    public commonError addAttendedGroup(String groupId) {
        if (attendedGroups.contains(groupId)) {
            return commonError.E_GROUP_EXISTING;
        }
        attendedGroups.add(groupId);
        return commonError.E_OK;
    }
    public commonError removeAttendedGroup(String groupId) {
        if (!attendedGroups.contains(groupId)) {
            return commonError.E_GROUP_NON_EXISTING;
        }
        attendedGroups.remove(groupId);
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

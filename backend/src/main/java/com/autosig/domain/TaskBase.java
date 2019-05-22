/** @file
 * Task domain class, which keeps all the basic information of a task.
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

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import com.alibaba.fastjson.JSONObject;
import com.autosig.util.UidGenerator;

@Document(collection = "tasks")
public class TaskBase {
    /** Uniformed Id */
    @Id
    public String uid;
    /** name (i.e main title) of this task */
    public String name;
    /** place where hold this task */
    public String place;
    /** indicate who hosts or manages this task */
    public String host;
    /** openId of creator */
    public String creatorOpenId;
    
    public TaskBase() {
    }
    public TaskBase(boolean allocUid) {
        if (allocUid)
            this.uid = UidGenerator.randomUid();
    }
    
    @Override
    public boolean equals(Object src) {
        return uid.compareTo(((TaskBase)src).uid) == 0;
    }
    
    /*
     * Getters/Setters
     */
    public String getUid() {
        return uid;
    }
    public String getName() {
        return name;
    }
    public String getPlace() {
        return place;
    }
    public String getHost() {
        return host;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getCreatorOpenId() {
        return creatorOpenId;
    }
    public void setCreatorOpenId(String creatorOpenId) {
        this.creatorOpenId = creatorOpenId;
    }
    
    public JSONObject getBasicInfo() {
        JSONObject info = new JSONObject();
        info.put("name", name);
        info.put("host", host);
        info.put("place", place);
        info.put("uid", uid);
        
        return info;
    }
}


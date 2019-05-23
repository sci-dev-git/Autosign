/** @file
 * Activity domain class, which keeps all the basic information of an activity.
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

@Document(collection = "activities")
public class ActivityBase {
    /** Uniformed Id */
    @Id
    public String uid;
    /** name (i.e main title) of this activity */
    public String name;
    /** place where hold this task */
    public String where;
    /** indicate who hosts or manages this task */
    public String host;
    /** time expression */
    public String timeExp;
    /** openId of creator */
    public String creatorOpenId;

    public ActivityBase(boolean allocUid) {
        if (allocUid)
            this.uid = UidGenerator.randomUid();
    }

    @Override
    public boolean equals(Object src) {
        return uid.compareTo(((ActivityBase)src).uid) == 0;
    }
    
    /*
     * Getters/Setters
     */
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getName() {
        return name;
    }
    
    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeExp() {
        return timeExp;
    }

    public void setTimeExp(String timeExp) {
        this.timeExp = timeExp;
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
        info.put("where", where);
        info.put("uid", uid);
        info.put("timeexp", timeExp);
        info.put("creatorOpenId", creatorOpenId);
        
        return info;
    }
}

/** @file
 * Group domain class, which keeps all the basic information of a group.
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

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import com.autosig.error.commonError;
import com.autosig.util.UidGenerator;

@Document(collection = "groups")
public class GroupBase {
    /** Standardized identify (i.e. UUID) */
    @Id
    public String uid;
    /** The name of this group */
    public String name;
    /** Short description */
    public String desc;
    /** openId of creator */
    public String creatorOpenId;
    /** place where created this group */
    public String place;
    /** list of Members references */
    @DBRef
    public List<UserBase> members;
    /** list of activities references */
    @DBRef
    public List<ActivityBase> activities;
    
    public GroupBase() {
        this.init();
    }
    public GroupBase(boolean allocUid) {
        if (allocUid)
            this.uid = UidGenerator.randomUid();
        this.init();
    }
    
    private void init() {
        this.members = new LinkedList<UserBase>();
        this.activities = new LinkedList<ActivityBase>();
    }
    
    @Override
    public boolean equals(Object src) {
        return uid.compareTo(((GroupBase)src).uid) == 0;
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
    
    public String getCreatorOpenId() {
        return creatorOpenId;
    }
    public void setCreatorOpenId(String creatorOpenId) {
        this.creatorOpenId = creatorOpenId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public List<UserBase> getMembers() {
        return members;
    }

    public List<ActivityBase> getActivities() {
        return activities;
    }

    /*
     * Operations on lists
     */
    public commonError addMember(UserBase user) {
        if (members.contains(user)) {
            return commonError.E_USER_EXISTING; /* disable duplicated adding */
        }
        this.members.add(user);
        return commonError.E_OK;
    }
    public commonError removeMember(UserBase user) {
        if (!members.contains(user)) {
            return commonError.E_USER_NON_EXISTING;
        }
        this.members.remove(user);
        return commonError.E_OK;
    }
    
    public commonError addActivity(ActivityBase activity) {
        if (activities.contains(activity)) {
            return commonError.E_ACTIVITY_EXISTING; /* disable duplicated adding */
        }
        this.activities.add(activity);
        return commonError.E_OK;
    }
    public commonError removeActivity(ActivityBase activity) {
        if (!activities.contains(activity)) {
            return commonError.E_ACTIVITY_NON_EXISTING;
        }
        this.activities.remove(activity);
        return commonError.E_OK;
    }
    
}

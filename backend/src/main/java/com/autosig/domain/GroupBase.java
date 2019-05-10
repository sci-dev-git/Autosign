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
    /** list of Members ID references */
    public List<String> members;
    /** list of activities ID references */
    public List<String> activities;
    
    public GroupBase() {
        this.init();
    }
    public GroupBase(boolean allocUid) {
        if (allocUid)
            this.uid = UidGenerator.randomUid();
        this.init();
    }
    
    private void init() {
        this.members = new LinkedList<String>();
        this.activities = new LinkedList<String>();
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

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<String> getActivities() {
        return activities;
    }

    /*
     * Operations on lists
     */
    public commonError addMember(String userId) {
        if (members.contains(userId)) {
            return commonError.E_USER_EXISTING; /* disable duplicated adding */
        }
        this.members.add(userId);
        return commonError.E_OK;
    }
    public commonError removeMember(String userId) {
        if (!members.contains(userId)) {
            return commonError.E_USER_NON_EXISTING;
        }
        this.members.remove(userId);
        return commonError.E_OK;
    }
    
    public commonError addActivity(String activityId) {
        if (activities.contains(activityId)) {
            return commonError.E_ACTIVITY_EXISTING; /* disable duplicated adding */
        }
        this.activities.add(activityId);
        return commonError.E_OK;
    }
    public commonError removeActivity(String activityId) {
        if (!activities.contains(activityId)) {
            return commonError.E_ACTIVITY_NON_EXISTING;
        }
        this.activities.remove(activityId);
        return commonError.E_OK;
    }
    
}

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

import java.util.List;
import java.util.LinkedList;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import com.autosig.error.commonError;
import com.autosig.util.UidGenerator;

@Document(collection = "activities")
public class ActivityBase {
    /** Uniformed Id */
    @Id
    public String uid;
    /** name (i.e main title) of this activity */
    public String name;
    /** list of Task references Id */
    List<String> tasks;

    public ActivityBase() {
        this.init();
    }
    public ActivityBase(boolean allocUid) {
        if (allocUid)
            this.uid = UidGenerator.randomUid();
        this.init();
    }
    
    private void init() {
        this.tasks = new LinkedList<String>();
    }
    
    /*
     * Operations on lists
     */
    public commonError addTask(String taskId) {
        if (tasks.contains(taskId)) {
            return commonError.E_TASK_EXISTING; /* disable duplicated adding */
        }
        this.tasks.add(taskId);
        return commonError.E_OK;
    }
    public commonError removeTask(String taskId) {
        if (!tasks.contains(taskId)) {
            return commonError.E_TASK_NON_EXISTING;
        }
        this.tasks.remove(taskId);
        return commonError.E_OK;
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

    public List<String> getTasks() {
        return tasks;
    }
}
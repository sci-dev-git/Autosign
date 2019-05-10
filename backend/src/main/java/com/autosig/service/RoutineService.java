/** @file
 * IRoutineService
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
package com.autosig.service;
import com.autosig.domain.UserBase;
import com.autosig.domain.GroupBase;
import com.autosig.domain.ActivityBase;
import com.autosig.domain.TaskBase;
import com.autosig.error.commonError;

public interface RoutineService {
    /**
     * Create a group in database.
     * @param group Target group.
     * @return common status code.
     */
    public commonError createGroup(GroupBase group);
    /**
     * Create a activity in database.
     * @param group Target activity.
     * @return common status code.
     */
    public commonError createActivity(ActivityBase activity);
    /**
     * Create a task in database.
     * @param group Target task.
     * @return common status code.
     */
    public commonError createTask(TaskBase task);
    
    /**
     * Delete a group from database. Warning: group will NOT be removed from UserBase.
     * @param group Target group.
     * @return common status code.
     */
    public commonError deleteGroup(GroupBase group);
    
    public GroupBase getGroupByUid(String uid);
    
    public ActivityBase getActivityByUid(String uid);
    
    public TaskBase getTaskByUid(String uid);
    
    /**
     * Add a User to Group as a new member.
     * @param group Target group
     * @param user Source user.
     * @return common status code.
     */
    public commonError addGroupMember(GroupBase group, UserBase user);
    
    /**
     * Remove a User from Group members.
     * @param group Target group
     * @param user Source user.
     * @return common status code.
     */
    public commonError removeGroupMember(GroupBase group, UserBase user);
    
    /**
     * Add a Activity to Group
     * @param group Target group
     * @param activity Source Activity
     * @return
     */
    public commonError addGroupActivity(GroupBase group, ActivityBase activity);
    
    /**
     * Remove a Activity from Group, also from database!
     * @param group Target group
     * @param activity Source Activity
     * @return
     */
    public commonError removeGroupActivity(GroupBase group, ActivityBase activity);
    
    /**
     * Add a Task to Activity
     * @param activity Target activity
     * @param task Source task
     * @return
     */
    public commonError addActivityTask(ActivityBase activity, TaskBase task);
    
    /**
     * Remove a Task from Activity, also from database!
     * @param activity Target activity
     * @param task Source task
     * @return
     */
    public commonError removeActivityTask(ActivityBase activity, TaskBase task);
    
    /**
     * Rename the Group.
     * @param group Target group
     * @param name New name string.
     */
    public commonError renameGroup(GroupBase group, String name);
}

/** @file
 * IUserService
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
import com.autosig.domain.ActivityBase;
import com.autosig.domain.GroupBase;
import com.autosig.domain.TaskBase;
import com.autosig.error.commonError;

public interface UserService {
    /**
     * Register a user.
     * @param user Target user.
     * @return common status code.
     */
    public commonError registerUser(UserBase user);

    /**
     * Get the openId of wx user.
     * @param wxcode Internal code acquired from wx.
     * @return openId if the authority is succeeded. Otherwise null pointer.
     */
    public String getOpenId(String wxcode);
    
    /**
     * Get the instance reference of a user by its OpenID.
     * @param id Target OpenID
     * @return reference to userbase
     */
    public UserBase getUserByOpenId(String id);
    
    /**
     * Create a group.
     * Build reference user <--> group
     * @param user Target User.
     * @param group Source group.
     * @return common status code.
     */
    public commonError createGroup(UserBase user, GroupBase group);
    
    /**
     * Delete a group created by user.
     * Break reference user <--> group
     * @param user Target User.
     * @param group Source Group
     * @return common status code.
     */
    public commonError deleteGroup(UserBase user, GroupBase group);
    
    /**
     * Attend a group.
     * Build reference user <--> group
     * @param user Target User.
     * @param group Source Group
     * @return common status code.
     */
    public commonError attendGroup(UserBase user, GroupBase group);
    /**
     * Quit a group.
     * Break reference user <--> group
     * @param user Target User.
     * @param group Source Group
     * @return common status code.
     */
    public commonError quitGroup(UserBase user, GroupBase group);
    
    /**
     * Create a activity in Group.
     * Build reference group <--> activity
     * @param group Target activity.
     * @param activity Source activity.
     * @return common status code.
     */
    public commonError createActivity(GroupBase group, ActivityBase activity);
    
    /**
     * Delete Activity from Group.
     * Break reference group <--> activity
     * @param group Target group
     * @param activity Source Activity
     * @return
     */
    public commonError deleteActivity(GroupBase group, ActivityBase activity);
    
    /**
     * Create a task in database.
     * Build reference activity <--> task
     * @param activity Target activity
     * @param task Source task
     * @return common status code.
     */
    public commonError createTask(ActivityBase activity, TaskBase task);
    
    /**
     * Delete Task
     * Break reference activity <--> task
     * @param activity Target activity
     * @param task Source task
     * @return
     */
    public commonError deleteTask(ActivityBase activity, TaskBase task);
}

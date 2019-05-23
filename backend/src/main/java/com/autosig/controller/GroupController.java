/** @file
 * Controller for /group/* APIs
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
package com.autosig.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.autosig.error.commonError;
import com.autosig.annotation.Authorization;
import com.autosig.annotation.CurrentGroup;
import com.autosig.annotation.CurrentUser;
import com.autosig.annotation.RoutineResolver;
import com.autosig.domain.ActivityBase;
import com.autosig.domain.GroupBase;
import com.autosig.domain.UserBase;
import com.autosig.service.RoutineService;
import com.autosig.service.UserService;
import com.autosig.util.ResponseWrapper;

@RestController
public class GroupController {
    @Autowired
    private RoutineService routineService;
    @Autowired
    private UserService userService;
    
    /**
     * API for group attendance
     * @param uid Uniformed ID of target Group.
     * @return
     */
    @RequestMapping(value = "/group/attend", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    @Authorization
    public String attendGroup(@CurrentUser UserBase user,
            @CurrentGroup GroupBase group) {

        commonError rc = userService.attendGroup(user, group);
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group quitting
     * @param uid Uniformed ID of target Group.
     * @return
     */
    @RequestMapping(value = "/group/quit", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    @Authorization
    public String quitGroup(@CurrentUser UserBase user,
            @CurrentGroup GroupBase group) {

        commonError rc = userService.quitGroup(user, group);
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group manager to remove a member
     * @param uid Uniformed ID of target Group.
     * @param openid OpenID of target user.
     * @return
     */
    @RequestMapping(value = "/group/remove_member", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    @Authorization
    public String removeMember(@CurrentUser UserBase user,
            @CurrentGroup GroupBase group,
            @RequestParam(value="openid") String openId) {
        
        if (group.getCreatorOpenId().compareTo(user.getOpenId()) != 0) { /* validate ownership */
            return ResponseWrapper.wrapResponse(commonError.E_PERMISSION_DENIED, null);
        }
        
        UserBase targetUser = userService.getUserByOpenId(openId);
        if (targetUser == null) {
            return ResponseWrapper.wrapResponse(commonError.E_USER_NON_EXISTING, null);
        }
        commonError rc = userService.quitGroup(targetUser, group);
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for Getting all Member joined in.
     * @param uid Uniformed ID of the target group
     * @return
     */
    @RequestMapping(value = "/group/get_members", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    public String getMembers(@CurrentGroup GroupBase group) {
        
        JSONObject body = new JSONObject();
        JSONArray users = new JSONArray();

        List<UserBase> members = group.getMembers();
        int size = members.size();
        for(int i=0; i < size; i++) {
            UserBase user = members.get(i);
            users.add(user.getBasicInfo());
        }
        
        body.put("size", users.size());
        body.put("users", users);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
    
    /**
     * API for group manager to add a new Activity.
     * @param uid Uniformed ID of target Group.
     * @param name Name of target Activity
     * @param where Indicate place where hold this task.
     * @param host Indicate who hosts or manages this task.
     * @param timeexp Time expression.
     */
    @RequestMapping(value = "/group/create_activity", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    @Authorization
    public String createActivity(@CurrentUser UserBase user,
            @CurrentGroup GroupBase group,
            @RequestParam(value="name") String name,
            @RequestParam(value="where") String where,
            @RequestParam(value="host") String host,
            @RequestParam(value="timeexp") String timeExp) {
        
        if (group.getCreatorOpenId().compareTo(user.getOpenId()) != 0) { /* validate ownership */
            return ResponseWrapper.wrapResponse(commonError.E_PERMISSION_DENIED, null);
        }
        
        ActivityBase activity = new ActivityBase(true);
        activity.setName(name);
        activity.setWhere(where);
        activity.setHost(host);
        activity.setTimeExp(timeExp);
        
        commonError rc = userService.createActivity(group, activity);
        if (rc.succeeded()) {
            JSONObject body = new JSONObject();
            body.put("uid", activity.getUid());
            return ResponseWrapper.wrapResponse(rc, body);
        }
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group manager to remove an Activity.
     * @param uid Uniformed ID of target Group.
     * @param activity_uid Uniformed ID of the Activity.
     * @return
     */
    @RequestMapping(value = "/group/remove_activity", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    @Authorization
    public String removeActivity(@CurrentUser UserBase user,
            @CurrentGroup GroupBase group,
            @RequestParam(value="activity_uid") String activityUid) {

        if (group.getCreatorOpenId().compareTo(user.getOpenId()) != 0) { /* validate ownership */
            return ResponseWrapper.wrapResponse(commonError.E_PERMISSION_DENIED, null);
        }
        ActivityBase activity = routineService.getActivityByUid(activityUid);
        if (activity == null) {
            return ResponseWrapper.wrapResponse(commonError.E_ACTIVITY_NON_EXISTING, null);
        }
        
        commonError rc = userService.deleteActivity(group, activity);
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group manager to get all the Activity created.
     * @param uid Uniformed ID of the target group
     * @return
     */
    @RequestMapping(value = "/group/get_activities", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    public String getActivities(@CurrentGroup GroupBase group) {
        
        JSONObject body = new JSONObject();
        List<ActivityBase> activities = new ArrayList<ActivityBase>();
        
        List<ActivityBase> activitys = group.getActivities();
        int size = activitys.size();
        for(int i=0; i < size; i++) {
            activities.add(activitys.get(i));
        }
        
        body.put("size", activities.size());
        body.put("activities", activities);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
    
    /**
     * API for group manager to edit Group information.
     * @param uid Uniformed ID of the target group
     * @param name New name string.
     * @param desc Description of group.
     * @return
     */
    @RequestMapping(value = "/group/update_info", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    @Authorization
    public String rename(@CurrentUser UserBase user,
            @CurrentGroup GroupBase group,
            @RequestParam(value="name") String name,
            @RequestParam(value="desc") String desc) {
        
        if (group.getCreatorOpenId().compareTo(user.getOpenId()) != 0) { /* validate ownership */
            return ResponseWrapper.wrapResponse(commonError.E_PERMISSION_DENIED, null);
        }
        
        commonError rc = routineService.updateGroupInfo(group, name, desc);
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group manager to get information of Group.
     * @param uid Uniformed ID of the target group
     * @return
     */
    @RequestMapping(value = "/group/info", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    public String info(@CurrentGroup GroupBase group) {
        JSONObject body = new JSONObject();
        body.put("group", group);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
}

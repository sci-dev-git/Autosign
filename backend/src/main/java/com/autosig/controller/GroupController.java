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
import com.autosig.annotation.CurrentUser;
import com.autosig.annotation.CurrentGroup;
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
     * API for group manager to attach a User as its member.
     * @paeam uid Uniformed ID of target Group.
     * @param usr_openid Uniformed ID of source user.
     * @return
     */
    @RequestMapping(value = "/group/add_member", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    public String addMember(@CurrentGroup GroupBase group,
            @RequestParam(value="usr_openid") String usrOpenId) {

        UserBase user = userService.getUserByOpenId(usrOpenId);
        if (user == null) {
            return ResponseWrapper.wrapResponse(commonError.E_USER_NON_EXISTING, null);
        }
        
        commonError rc = routineService.addGroupMember(group, user);
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group manager to remove a member User.
     * @paeam uid Uniformed ID of target Group.
     * @param usr_openid Uniformed ID of source user.
     * @return
     */
    @RequestMapping(value = "/group/remove_member", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    public String removeMember(@CurrentGroup GroupBase group,
            @RequestParam(value="usr_openid") String usrOpenId) {

        UserBase user = userService.getUserByOpenId(usrOpenId);
        if (user == null) {
            return ResponseWrapper.wrapResponse(commonError.E_USER_NON_EXISTING, null);
        }
        
        commonError rc = routineService.removeGroupMember(group, user);
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

        List<String> memberIds = group.getMembers(); /* resolve instances of all the users */
        for(int i=0; i < memberIds.size(); i++) {
            UserBase user = userService.getUserByOpenId(memberIds.get(i));
            users.add(user.getBasicInfo());
        }
        
        body.put("size", users.size());
        body.put("users", users);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
    
    /**
     * API for group manager to add a new Activity.
     * @paeam uid Uniformed ID of target Group.
     * @paeam name Name of target Activity
     * @return uid = Uniformed ID of the Activity.
     */
    @RequestMapping(value = "/group/create_activity", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    public String createActivity(@CurrentGroup GroupBase group,
            @RequestParam(value="name") String name) {
        
        ActivityBase activity = new ActivityBase(true); /* create instance of activity */
        activity.setName(name);
        
        commonError rc = routineService.createActivity(activity);
        if (rc.succeeded()) {
            rc = routineService.addGroupActivity(group, activity);
            
            JSONObject body = new JSONObject();
            body.put("uid", activity.getUid());
            return ResponseWrapper.wrapResponse(rc, body);
        }
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group manager to remove an Activity.
     * @paeam uid Uniformed ID of target Group.
     * @paeam activity_uid Uniformed ID of the Activity.
     * @return
     */
    @RequestMapping(value = "/group/remove_activity", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    public String removeActivity(@CurrentGroup GroupBase group,
            @RequestParam(value="activity_uid") String activityUid) {

        ActivityBase activity = routineService.getActivityByUid(activityUid);
        if (activity == null) {
            return ResponseWrapper.wrapResponse(commonError.E_ACTIVITY_NON_EXISTING, null);
        }
        
        commonError rc = routineService.removeGroupActivity(group, activity);
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
        
        List<String> activityIds = group.getActivities();
        for(int i=0; i < activityIds.size(); i++) {
            activities.add(routineService.getActivityByUid(activityIds.get(i)) );
        }
        
        body.put("size", activities.size());
        body.put("activities", activities);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
}

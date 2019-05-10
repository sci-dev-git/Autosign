/** @file
 * Controller for /usr/* APIs
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

import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.autosig.service.UserService;
import com.autosig.service.RoutineService;
import com.autosig.service.TokenService;
import com.autosig.util.ResponseWrapper;
import com.autosig.domain.GroupBase;
import com.autosig.domain.UserBase;
import com.autosig.domain.UserType;
import com.autosig.error.commonError;
import com.autosig.annotation.CurrentUser;
import com.autosig.annotation.Authorization;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RoutineService routineService;

    /**
     * API for User Registration.
     * @param openid Uniformed OpenID of the new user.
     * @param type User type (Attendee or Manager)
     * @param code Secondary ID of the user.
     * @param real_name Real name of this user.
     * @param password Encoded password.
     * @return
     */
    @RequestMapping(value = "/usr/reg", method = RequestMethod.GET)
    public String reg(@RequestParam(value="openid") String openId,
                          @RequestParam(value="type") int type,
                          @RequestParam(value="code") String code,
                          @RequestParam(value="real_name") String real_name,
                          @RequestParam(value="password") String password) {
        if (type < 0 || type > 1) {
            return ResponseWrapper.wrapResponse(commonError.E_FAULT, null);
        }
    
        UserBase user = new UserBase();
        UserType userType = UserType.values()[type];
    
        user.setOpenId(openId);
        user.setType(userType);
        user.setCode(code);
        user.setRealName(real_name);
        user.setPassword(password);
        
        commonError rc = userService.registerUser(user);
        return ResponseWrapper.wrapResponse(rc, null);
    }
  
    /**
     * User Login authorization.
     * @param openid Uniformed OpenID of the new user.
     * @param code Secondary ID of the user.
     * @param password Encoded password.
     * @return
     */
    @RequestMapping(value = "/usr/login", method = RequestMethod.GET)
    public String login(@RequestParam(value="openid") String openId,
                              @RequestParam(value="code") String code,
                              @RequestParam(value="password") String password) {
        JSONObject body = new JSONObject();
      
        /* authorized the identify of user */
        commonError result = userService.authorizeUser(openId, code, password);
        
        if (result == commonError.E_OK) {
            String token = tokenService.createToken(openId); /* create token for later accessing */
            body.put("token", token);
        }
        
        return ResponseWrapper.wrapResponse(result, body);
    }
    
    /**
     * User Logout.
     * @param openid Uniformed OpenID of the new user.
     * @return
     */
    @RequestMapping(value = "/usr/logout", method = RequestMethod.GET)
    @Authorization
    public String logout(@CurrentUser UserBase user) {
        tokenService.deauthToken(user.getOpenId());
        return ResponseWrapper.wrapResponse(commonError.E_OK, null);
    }
    
    
    /**
     * API for Group Creation.
     * @param name Name of the target group.
     * @return uid = Uniformed ID of the new group
     */
    @RequestMapping(value = "/usr/create_group", method = RequestMethod.GET)
    @Authorization(userLimited = true, userType = UserType.USER_MANAGER)
    
    public String createGroup(@CurrentUser UserBase user,
            @RequestParam(value="name") String name) {
        
        GroupBase group = new GroupBase(true); /* create instance of group */
        group.setName(name);

        commonError rc = routineService.createGroup(group);
        if (rc.succeeded()) {
            
            rc = userService.addGroup(user, group);
            if (rc.succeeded()) {
                JSONObject body = new JSONObject();
                body.put("uid", group.getUid());
                return ResponseWrapper.wrapResponse(rc, body);
            }
        }
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for Group Removal.
     * @param uid Uniformed ID of the target group
     * @return
     */
    @RequestMapping(value = "/usr/remove_group", method = RequestMethod.GET)
    @Authorization(userLimited = true, userType = UserType.USER_MANAGER)
    
    public String removeGroup(@CurrentUser UserBase user,
            @RequestParam(value="uid") String uid) {
        
        GroupBase group = routineService.getGroupByUid(uid);
        if (group == null) {
            return ResponseWrapper.wrapResponse(commonError.E_GROUP_NON_EXISTING, null);
        }
        
        commonError rc = userService.deleteGroup(user, group); /* FIXME: deal with this dependency */
        if (rc.succeeded()) {
            rc = routineService.deleteGroup(group);
        }
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for Getting all Groups created.
     * @param uid Uniformed ID of the target group
     * @return
     */
    @RequestMapping(value = "/usr/get_groups", method = RequestMethod.GET)
    @Authorization(userLimited = true, userType = UserType.USER_MANAGER)
    
    public String getGroups(@CurrentUser UserBase user,
            @RequestParam(value="uid") String uid) {
        
        JSONObject body = new JSONObject();
        List<GroupBase> groups = new ArrayList<GroupBase>();
        List<String> groupIds = user.getGroups();
        
        for(int i=0; i < groupIds.size();i++) {
            groups.add(routineService.getGroupByUid(groupIds.get(i)) );
        }
        
        body.put("size", groups.size());
        body.put("groups", groups);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
}

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
import com.autosig.error.commonError;
import com.autosig.annotation.CurrentUser;
import com.autosig.annotation.RoutineResolver;
import com.autosig.annotation.Authorization;
import com.autosig.annotation.CurrentGroup;

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
     * @param openId OpenId acquired from login.
     * @param place Registration place.
     * @param code Secondary ID of the user.
     * @param real_name Real name of this user.
     * @param password Encoded password.
     * @return
     */
    @RequestMapping(value = "/usr/reg", method = RequestMethod.GET)
    public String reg(@RequestParam(value="openid") String openId,
                         @RequestParam(value="place") String place,
                          @RequestParam(value="code") String code,
                          @RequestParam(value="real_name") String real_name) {

        UserBase user = new UserBase();
    
        user.setOpenId(openId);
        user.setPlace(place);
        user.setCode(code);
        user.setRealName(real_name);
        
        commonError rc = userService.registerUser(user);
        return ResponseWrapper.wrapResponse(rc, null);
    }
  
    /**
     * User Login authorization.
     * @param wxcode Internal code acquired from wx.
     * @return
     */
    @RequestMapping(value = "/usr/login", method = RequestMethod.GET)
    public String login(@RequestParam(value="wxcode") String wxcode) {
        String openId = userService.getOpenId(wxcode);
        if (openId != null) {
            JSONObject body = new JSONObject();
            body.put("openId", openId);

            /* Authorized the identify of user */
            UserBase user = userService.getUserByOpenId(openId);
            
            if (user != null) {
                String token = tokenService.createToken(openId); /* create token for later accessing */
                body.put("token", token);
                return ResponseWrapper.wrapResponse(commonError.E_OK, body);
            } else
                return ResponseWrapper.wrapResponse(commonError.E_USER_NON_EXISTING, body);
        } else
            return ResponseWrapper.wrapResponse(commonError.E_FAULT, null);
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
    
    @RequestMapping(value = "/usr/door", method = RequestMethod.GET)
    public String door(@RequestParam(value="openid") String openId) {
        
        JSONObject body = new JSONObject();
        body.put("openId", openId);

        /* Authorized the identify of user */
        UserBase user = userService.getUserByOpenId(openId);
        
        if (user != null) {
            String token = tokenService.createToken(openId); /* create token for later accessing */
            body.put("token", token);
            return ResponseWrapper.wrapResponse(commonError.E_OK, body);
        } else
            return ResponseWrapper.wrapResponse(commonError.E_USER_NON_EXISTING, body);
    }
    
    
    /**
     * API for Group Creation.
     * @param name Name of the target group.
     * @return uid = Uniformed ID of the new group
     */
    @RequestMapping(value = "/usr/create_group", method = RequestMethod.GET)
    @Authorization
    public String createGroup(@CurrentUser UserBase user,
            @RequestParam(value="name") String name) {
        
        GroupBase group = new GroupBase(true);
        group.setName(name);

        commonError rc = userService.createGroup(user, group);
        if (rc.succeeded()) {
            JSONObject body = new JSONObject();
            body.put("uid", group.getUid());
            return ResponseWrapper.wrapResponse(rc, body);
        }
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for Group Removal.
     * @param uid Uniformed ID of the target group
     * @return
     */
    @RequestMapping(value = "/usr/remove_group", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.GROUP)
    @Authorization
    public String removeGroup(@CurrentUser UserBase user,
            @CurrentGroup GroupBase group) {
        
        if (group.getCreatorOpenId().compareTo(user.getOpenId()) != 0) { /* validate ownership */
            return ResponseWrapper.wrapResponse(commonError.E_PERMISSION_DENIED, null);
        }
        
        commonError rc = userService.deleteGroup(user, group);
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for Getting all Groups created by this user.
     * @return
     */
    @RequestMapping(value = "/usr/get_created_groups", method = RequestMethod.GET)
    @Authorization
    public String getCreatedGroups(@CurrentUser UserBase user) {
        
        JSONObject body = new JSONObject();
        
        /*
         * Resolve all the groups from repository
         */
        List<GroupBase> groups = new ArrayList<GroupBase>();
        List<String> groupIds = user.getCreatedGroups();
        int size = groupIds.size();
        for(int i=0; i < size; i++) {
            groups.add(routineService.getGroupByUid(groupIds.get(i)));
        }
        
        body.put("size", groups.size());
        body.put("groups", groups);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
    
    /**
     * API for getting information of User.
     * @param openid Uniformed ID of the target user
     * @return
     */
    @RequestMapping(value = "/usr/info", method = RequestMethod.GET)
    public String info(@RequestParam(value="openid") String openId) {
        
        UserBase user = userService.getUserByOpenId(openId);
        if (user == null) {
            return ResponseWrapper.wrapResponse(commonError.E_USER_NON_EXISTING, null);
        }
        
        JSONObject body = new JSONObject();
        body.put("usr", user.getBasicInfo());
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
}

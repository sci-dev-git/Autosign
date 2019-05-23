/** @file
 * Controller for /activity/* APIs
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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.autosig.error.commonError;
import com.autosig.annotation.RoutineResolver;
import com.autosig.annotation.CurrentActivity;
import com.autosig.domain.ActivityBase;
import com.autosig.util.ResponseWrapper;

@RestController
public class ActivityController {
    /**
     * API for group manager to get information of Activity.
     * @param uid Uniformed ID of the target group
     * @return
     */
    @RequestMapping(value = "/activity/info", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.ACTIVITY)
    public String info(@CurrentActivity ActivityBase activity) {
        JSONObject body = new JSONObject();
        body.put("activity", activity);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
}

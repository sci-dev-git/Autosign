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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.autosig.error.commonError;
import com.autosig.annotation.RoutineResolver;
import com.autosig.annotation.Authorization;
import com.autosig.annotation.CurrentActivity;
import com.autosig.domain.ActivityBase;
import com.autosig.domain.TaskBase;
import com.autosig.domain.UserType;
import com.autosig.service.RoutineService;
import com.autosig.util.ResponseWrapper;

@RestController
public class ActivityController {
    @Autowired
    private RoutineService routineService;

    /**
     * API for group manager to add a new Task in Activity.
     * @paeam uid Uniformed ID of target Activity.
     * @paeam name Name of target Task
     * @return uid = Uniformed ID of the Task.
     */
    @RequestMapping(value = "/activity/create_task", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.ACTIVITY)
    @Authorization(userLimited = true, userType = UserType.USER_MANAGER)
    public String createTask(@CurrentActivity ActivityBase activity,
            @RequestParam(value="name") String name) {

        TaskBase task = new TaskBase(true); /* create instance of task */
        task.setName(name);
        
        commonError rc = routineService.createTask(task);
        if (rc.succeeded()) {
            rc = routineService.addActivityTask(activity, task);
            
            JSONObject body = new JSONObject();
            body.put("uid", task.getUid());
            return ResponseWrapper.wrapResponse(rc, body);
        }
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group manager to remove a new Task in Activity.
     * @paeam uid Uniformed ID of target Group.
     * @paeam task_uid Uniformed ID of the Activity.
     * @return
     */
    @RequestMapping(value = "/activity/remove_task", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.ACTIVITY)
    @Authorization(userLimited = true, userType = UserType.USER_MANAGER)
    public String removeTask(@CurrentActivity ActivityBase activity,
            @RequestParam(value="task_uid") String taskUid) {

        TaskBase task = routineService.getTaskByUid(taskUid);
        if (task == null) {
            return ResponseWrapper.wrapResponse(commonError.E_TASK_NON_EXISTING, null);
        }
        
        commonError rc = routineService.removeActivityTask(activity, task);
        return ResponseWrapper.wrapResponse(rc, null);
    }
    
    /**
     * API for group manager to get all the Tasks in Activity.
     * @param uid Uniformed ID of the target group
     * @return
     */
    @RequestMapping(value = "/activity/get_tasks", method = RequestMethod.GET)
    @RoutineResolver(type = RoutineResolver.routineType.ACTIVITY)
    public String getTasks(@CurrentActivity ActivityBase activity) {
        
        JSONObject body = new JSONObject();
        List<TaskBase> tasks = new ArrayList<TaskBase>();
        
        List<String> taskIds = activity.getTasks();
        for(int i=0; i < taskIds.size(); i++) {
            tasks.add(routineService.getTaskByUid(taskIds.get(i)) );
        }
        
        body.put("size", tasks.size());
        body.put("tasks", tasks);
        return ResponseWrapper.wrapResponse(commonError.E_OK, body);
    }
    
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

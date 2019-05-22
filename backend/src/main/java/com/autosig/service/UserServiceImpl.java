/** @file
 * service - User management.
 */
/*
 *  Autosig (Backend server for autosig management program in WeChat-App)
 *  Copyright (C) 2019, TYUT-404 Team. Developer <diyer175@hotmail.com>.
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

import java.util.List;

import com.autosig.repository.ActivityRepository;
import com.autosig.repository.GroupRepository;
import com.autosig.repository.TaskRepository;
import com.autosig.repository.UserRepository;
import com.autosig.domain.UserBase;
import com.autosig.domain.ActivityBase;
import com.autosig.domain.GroupBase;
import com.autosig.domain.TaskBase;
import com.autosig.error.commonError;
import com.autosig.util.HttpRequest;
import com.autosig.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Service
public class UserServiceImpl implements UserService {
	
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private TaskRepository taskRepository;
    
    public commonError registerUser(UserBase user) {
        try {
            UserBase previousUser = this.getUserByOpenId(user.getOpenId());
            if (previousUser == null) {
                userRepository.save(user);
                return commonError.E_OK; /* succeeded */
            } else {
                return commonError.E_USER_EXISTING; /* this user has been registered before. */
            }
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    
    public String getOpenId(String wxcode) {
        String url = String.format(Constants.wx_auth_code2session_url, Constants.wx_APPID, Constants.wx_SECRET, wxcode);
        try {
            String response = HttpRequest.get(url);
            JSONObject resp = JSON.parseObject(response);
            
            if (resp == null || resp.containsKey("errcode")) {
                return null;
            } else
                return resp.getString("openid");
        } catch(Exception exp) {
            return null;
        }
    }

    public UserBase getUserByOpenId(String id) {
        try {
            return userRepository.findByOpenId(id);
        } catch(Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
    
    public commonError createGroup(UserBase user, GroupBase group) {
        group.setCreatorOpenId(user.getOpenId());
        group.setPlace(user.getPlace());
        
        /*
         * Validate if there has been already a group taking the same name of new one.
         */
        int size = user.getCreatedGroups().size();
        List<String> groups = user.getCreatedGroups();
        for(int i=0; i < size; i++) {
            GroupBase origin = this.groupRepository.findByUid(groups.get(i));
            if (origin.getName().compareTo(group.getName()) == 0) {
                return commonError.E_GROUP_EXISTING;
            }
        }
        
        commonError rc = user.addCreatedGroup(group.getUid()); // add reference to its creator
        if (rc.succeeded()) {
            try {
                userRepository.save(user);
                groupRepository.save(group);
            } catch(Exception exp) {
                exp.printStackTrace();
                return commonError.E_FAULT;
            }
        }
        return rc;
    }
    
    public commonError deleteGroup(UserBase user, GroupBase group) {
        String groupId = group.getUid();
        
        commonError rc = user.removeCreatedGroup(groupId); // remove reference from its creator
        if (rc.succeeded()) {
            /*
             * Quit all the members
             */
            List<UserBase> users = group.getMembers();
            int size = users.size();
            for(int i=0; i < size; i++) {
                UserBase attendee = users.get(i);
                if (attendee.equals(user)) { /* when the attendee is concurrently the manager. */
                    attendee = user;
                }
                rc = attendee.removeAttendedGroup(groupId); // @store(1)
                if (rc.failed()) {
                    attendee.addAttendedGroup(groupId); // @restore(1)
                    return rc;
                }
                try {
                    userRepository.save(attendee);
                } catch(Exception exp) {
                    exp.printStackTrace();
                    return commonError.E_FAULT;
                }
            }
            /*
             * Delete all the activities belongs to this activity
             */
            List<ActivityBase> activities = group.getActivities();
            size = activities.size();
            for(int i=0; i < size; i++) {
                rc = this.deleteActivity(group, activities.get(i));
                if (rc.failed()) {
                    return rc; // FIXME how about restoration?
                }
            }
            
            try {
                userRepository.save(user);
                groupRepository.deleteByUid(groupId);
            } catch(Exception exp) {
                exp.printStackTrace();
                return commonError.E_FAULT;
            }
        }
        return rc;
    }
    
    public commonError attendGroup(UserBase user, GroupBase group) {
        
        commonError rc = group.addMember(user); // @store(1)
        if (rc.succeeded()) {

            rc = user.addAttendedGroup(group.getUid()); // add reference to attendee
            if (rc.succeeded()) {
                try {
                    userRepository.save(user);
                    groupRepository.save(group);
                } catch(Exception exp) {
                    exp.printStackTrace();
                    return commonError.E_FAULT;
                }
            } else {
                group.removeMember(user); // @ restore(1)
            }
        }
        return rc;
        
    }

    public commonError quitGroup(UserBase user, GroupBase group) {
        
        commonError rc = group.removeMember(user); // @store(1)
        if (rc.succeeded()) {
            
            rc = user.removeAttendedGroup(group.getUid()); // add reference from attendee
            if (rc.succeeded()) {
                try {
                    userRepository.save(user);
                    groupRepository.save(group);
                } catch(Exception exp) {
                    exp.printStackTrace();
                    return commonError.E_FAULT;
                }
            } else {
                group.addMember(user); // @restore(1)
            }
        }
        return rc;
    }
    
    public commonError createActivity(GroupBase group, ActivityBase activity) {
        commonError rc = group.addActivity(activity); // add reference to group
        if (rc.succeeded()) {
            try {
                activityRepository.save(activity);
                groupRepository.save(group);
            } catch(Exception exp) {
                exp.printStackTrace();
                return commonError.E_FAULT;
            }
        }
        return commonError.E_OK; /* succeeded */
    }
    
    public commonError deleteActivity(GroupBase group, ActivityBase activity) {
        commonError rc = group.removeActivity(activity); // remove reference from group
        if (rc.succeeded()) {
            /*
             * Delete tasks belongs to this activity
             */
            List<TaskBase> tasks = activity.getTasks();
            int size = tasks.size();
            for(int i=0; i < size; i++) {
                rc = this.deleteTask(activity, tasks.get(i));
                if (rc.failed()) {
                    return rc; // FIXME how about restoration?
                }
            }
            try {
                groupRepository.save(group);
                activityRepository.delete(activity);
            } catch(Exception exp) {
                exp.printStackTrace();
                return commonError.E_FAULT;
            }
        }
        return rc;
    }
    
    public commonError createTask(ActivityBase activity, TaskBase task) {
        commonError rc = activity.addTask(task); // add reference to activity
        if (rc.succeeded()) {
            try {
                taskRepository.save(task);
                activityRepository.save(activity);
            } catch(Exception exp) {
                exp.printStackTrace();
                return commonError.E_FAULT;
            }
        }
        return commonError.E_OK; /* succeeded */
    }
    
    public commonError deleteTask(ActivityBase activity, TaskBase task) {
        commonError rc = activity.removeTask(task); // remove reference from activity
        if (rc.succeeded()) {
            try {
                activityRepository.save(activity);
                taskRepository.delete(task);
            } catch(Exception exp) {
                exp.printStackTrace();
                return commonError.E_FAULT;
            }
        }
        return rc;
    }
    
}

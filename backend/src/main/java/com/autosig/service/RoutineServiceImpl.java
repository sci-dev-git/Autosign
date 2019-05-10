/** @file
 * service - Routine management.
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

import com.autosig.repository.GroupRepository;
import com.autosig.repository.ActivityRepository;
import com.autosig.repository.TaskRepository;
import com.autosig.domain.ActivityBase;
import com.autosig.domain.GroupBase;
import com.autosig.domain.TaskBase;
import com.autosig.domain.UserBase;
import com.autosig.domain.UserType;
import com.autosig.error.commonError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoutineServiceImpl implements RoutineService {
	
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private TaskRepository taskRepository;
    
    /*
     * Create entry in database
     */
    public commonError createGroup(GroupBase group) {
        try {
            groupRepository.save(group);
            return commonError.E_OK; /* succeeded */
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    public commonError createActivity(ActivityBase activity) {
        try {
            activityRepository.save(activity);
            return commonError.E_OK; /* succeeded */
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    public commonError createTask(TaskBase task) {
        try {
            taskRepository.save(task);
            return commonError.E_OK; /* succeeded */
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    
    public commonError deleteGroup(GroupBase group) {
        try {
            groupRepository.deleteByUid(group.getUid());
            return commonError.E_OK; /* succeeded */
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }

    /*
     * Get instance from database
     */
    public GroupBase getGroupByUid(String uid) {
        try {
            return groupRepository.findByUid(uid);
        } catch(Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
    public ActivityBase getActivityByUid(String uid) {
        try {
            return activityRepository.findByUid(uid);
        } catch(Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
    public TaskBase getTaskByUid(String uid) {
        try {
            return taskRepository.findByUid(uid);
        } catch(Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
    
    /*
     * Add/Remove Member to/from Group
     */
    public commonError addGroupMember(GroupBase group, UserBase user) {
        try {
            if (user.getType() != UserType.USER_ATTENDEE) {
                return commonError.E_PERMISSION_DENIED; /* the user must be an attendee */
            }
            commonError rc = group.addMember(user.getOpenId());
            if (rc.succeeded()) {
                groupRepository.save(group);
            }
            return rc;
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    public commonError removeGroupMember(GroupBase group, UserBase user) {
        try {
            commonError rc = group.removeMember(user.getOpenId());
            if (rc.succeeded()) {
                groupRepository.save(group);
            }
            return rc;
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    
    /*
     * Add/Remove Activity to/from Group
     */
    public commonError addGroupActivity(GroupBase group, ActivityBase activity) {
        try {
            commonError rc = group.addActivity(activity.getUid());
            if (rc.succeeded()) {
                groupRepository.save(group);
            }
            return rc;
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    public commonError removeGroupActivity(GroupBase group, ActivityBase activity) {
        try {
            commonError rc = group.removeActivity(activity.getUid());
            if (rc.succeeded()) {
                groupRepository.save(group);
                
                activityRepository.delete(activity); /* definitely remove from repository */
            }
            return rc;
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    
    /*
     * Add/Remove Task to/from Activity
     */
    public commonError addActivityTask(ActivityBase activity, TaskBase task) {
        try {
            commonError rc = activity.addTask(task.getUid());
            if (rc.succeeded()) {
                activityRepository.save(activity);
            }
            return rc;
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    public commonError removeActivityTask(ActivityBase activity, TaskBase task) {
        try {
            commonError rc = activity.removeTask(task.getUid());
            if (rc.succeeded()) {
                activityRepository.save(activity);
                
                taskRepository.delete(task); /* definitely remove from repository */
            }
            return rc;
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
    
    public commonError renameGroup(GroupBase group, String name) {
        try {
            group.setName(name);
            groupRepository.save(group);
            return commonError.E_OK;
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
}

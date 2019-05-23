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
import com.autosig.domain.ActivityBase;
import com.autosig.domain.GroupBase;
import com.autosig.error.commonError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoutineServiceImpl implements RoutineService {
	
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ActivityRepository activityRepository;

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

    public commonError updateGroupInfo(GroupBase group, String name, String desc) {
        try {
            group.setName(name);
            group.setDesc(desc);
            groupRepository.save(group);
            return commonError.E_OK;
        } catch(Exception exp) {
            exp.printStackTrace();
            return commonError.E_FAULT;
        }
    }
}

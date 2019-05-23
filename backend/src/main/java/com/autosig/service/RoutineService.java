/** @file
 * IRoutineService
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
import com.autosig.domain.GroupBase;
import com.alibaba.fastjson.JSONObject;
import com.autosig.domain.ActivityBase;
import com.autosig.error.commonError;

/**
 * Note that functions prefixed with:
 *   1. create/delete will operate directly in repository, buy NOT manage
 *      reference correlation among domains.
 *   2. ref will only manage references .
 *   3. deref not only deals with references but also repository.
 */
public interface RoutineService {
    
    /**
     * Get instance of Group by uid.
     * @param uid
     * @return
     */
    public GroupBase getGroupByUid(String uid);
    
    /**
     * Get instance of Activity of Group by uid.
     * @param uid
     * @return
     */
    public ActivityBase getActivityByUid(String uid);

    /**
     * Update the information of a Group.
     * @param group Target group
     * @param name New name string.
     * @param desc Descriptions.
     */
    public commonError updateGroupInfo(GroupBase group, String name, String desc);
}

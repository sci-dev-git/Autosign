/** @file
 * IGroupRepository
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
package com.autosig.repository;
import java.util.List;
import com.autosig.domain.GroupBase;
import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.data.mongodb.repository.Query;

public interface GroupRepository extends MongoRepository<GroupBase, String> {
    /**
     * Find Group by uid
     * @param uuid
     * @return GroupBase referenced to the user data
     */
    public GroupBase findByUid(String uid);
    
    /**
     * Find group by name keyword
     * @param place The created place of group.
     * @param name Name keywords.
     */
    // @Query(value = "{ place:{$eq:?0}, name:{$regex:?1} }")
    public List<GroupBase> findByPlaceAndNameLike(String place, String name);
    
    /**
     * Find group by place where it was created.
     * @param place
     * @return
     */
    public List<GroupBase> findByPlace(String place);

    /**
     * Delete Group by uid
     * @param uuid.
     */
    public void deleteByUid(String uid);
}

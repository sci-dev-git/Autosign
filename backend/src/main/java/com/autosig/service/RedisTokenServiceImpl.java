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

import com.autosig.util.Constants;
import com.autosig.util.UidGenerator;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class RedisTokenServiceImpl implements TokenService {

    @Autowired
    private RedisTemplate<String, String> redis;

    public String createToken(String openId) {
        String uuid = UidGenerator.randomUid();
        String token = openId +  '_' + uuid;
        
        redis.boundValueOps(openId).set(uuid, Constants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS);
        return token;
    }

    /**
     * Parse the token that consists of openId and uuid.
     * @param token Target token to be parsed
     * @return reference to an array, [0] is openId and [1] is uuid.
     */
    private String[] parseToken(String token) {
        if (token == null || token.length() == 0) {
            return null;
        }
        String[] param = token.split("_");
        if (param.length != 2) {
            return null;
        }
        return param;
    }
    
    public String getOpenId(String token) {
        String[] param = parseToken(token);
        if (param == null)
            return null;
        return param[0];
    }
    
    public boolean authToken(String token) {
        String[] param = parseToken(token);
        if (param == null)
            return false;
      
        String openId = param[0];
        String uuid = param[1];
        
        String localUUID = redis.boundValueOps(openId).get();
        if (localUUID == null || !localUUID.equals(uuid)) {
            return false;
        }
        /*
         * If the operation is successful, witch means the authorization is valid,
         * so expand the expiration time of token.
         */
        redis.boundValueOps(openId).expire(Constants.TOKEN_EXPIRES_HOUR, TimeUnit.HOURS);
        return true;
    }

    public void deauthToken(String openId) {
        redis.delete(openId);
    }
}

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

import com.autosig.repository.UserRepository;
import com.autosig.domain.UserBase;
import com.autosig.error.authorizationError;
import com.autosig.error.commonError;
import com.autosig.manager.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TokenManager tokenManager;
	
	public commonError registerUser(UserBase user) {
		try {
            UserBase previousUser = this.getUserByOpenId(user.getOpenId());
            if (previousUser == null) {
                userRepository.save(user);
                return commonError.E_OK; /* succeeded */
            } else
                return commonError.E_USER_EXISTING; /* this user has been registered before. */
		} catch(Exception exp) {
			exp.printStackTrace();
			return commonError.E_FAULT;
		}
	}
	
	public authorizationError authorizeUser(String openId, String code, String password) {
		authorizationError result = new authorizationError();
		result.error = commonError.E_OK; 
		
		/*
		 * Validate the basic information
		 */
		UserBase user = this.getUserByOpenId(openId);
		
		if (user == null || !user.getCode().equals(code))
			result.error = commonError.E_USER_NON_EXISTING;
		if (!user.getPassword().equals(password))
			result.error = commonError.E_PASSWORD_INVALID;
		
		/*
		 * Generate the token for the authorization of next accessing.
		 */
		if (result.error == commonError.E_OK) {
			result.token = tokenManager.createToken(openId);
		} else
			result.token = null;
		
		return result;
	}
	
	public void deauthUser(String openId) {
		tokenManager.deauthToken(openId);
	}
	
	public UserBase getUserByOpenId(String id) {
		try {
			return userRepository.findByOpenId(id);
		} catch(Exception exp) {
			exp.printStackTrace();
			return null;
		}
	}
}
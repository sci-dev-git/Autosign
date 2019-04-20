/** @file
 * IUserService
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
import com.autosig.domain.UserBase;
import com.autosig.error.commonError;
import com.autosig.error.authorizationError;

public interface UserService {
	/**
	 * Register a user.
	 * @param user Target user.
	 * @return common error code.
	 */
	public commonError registerUser(UserBase user);
	
	/**
	 * User authority.
	 * @param openId Target OpenID acquired by wx.
	 * @param code Secondary ID.
	 * @param password Encoded password.
	 * @return authorization error pack.
	 */
	public authorizationError authorizeUser(String openId, String code, String password);
	
	/**
	 * User Deauthorization.
	 * @param openId Target OpenID acquired by wx.
	 */
	public void deauthUser(String openId);
	
	/**
	 * Get the information of a user by its OpenID.
	 * @param id Target OpenID
	 * @return reference to userbase
	 */
	public UserBase getUserByOpenId(String id);
}

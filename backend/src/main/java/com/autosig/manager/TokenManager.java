/** @file
 * ITokenManager
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
package com.autosig.manager;

public interface TokenManager {
	/**
	 * Create a token for logging.
	 * @param openId
	 * @return token created.
	 */
    public String createToken(String openId);
    
    /**
     * Authorize token
     * @param token
     * @return true if valid.
     */
    public boolean authToken(String token);

    /**
     * Get openId from token.
     * @param token
     * @return openId.
     */
    public String getOpenId(String token);
    
    /**
     * Delete the token from cache by openId.
     * @param token
     */
    public void deauthToken(String openId);
}

/** @file
 * This file contains all the constant values of application.
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
package com.autosig.util;

public class Constants {
    public static final long TOKEN_EXPIRES_HOUR = 6;
    public static final long SECS_PER_HOURS = 3600;
    public static final int CHUNK_SIZE = 8192;
    public static final String HTTP_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)";
    
    /*
     * WX auth configurations
     */
    public static String wx_auth_code2session_url = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    public static String wx_APPID = "wx2b37f78e61f4a7b5";
    public static String wx_SECRET = "05e42ccf5182550ee3eceae283ef42e1";
}

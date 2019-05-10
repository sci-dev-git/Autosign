/** @file
 * Filter for token authorization, for methods that is annotated with @Authorization
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
package com.autosig.config;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.autosig.annotation.Authorization;
import com.autosig.error.commonError;
import com.autosig.repository.UserRepository;
import com.autosig.service.TokenService;
import com.autosig.domain.UserBase;
import com.autosig.util.ResponseWrapper;

/**
 * Customed Authorization Interceptor, witch filter out the non-logged-in request.
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        /* Passing when not a handler method */
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        
        /* do filter only when the method is annotated with @Authorization. */
        Authorization authAnnotation = method.getAnnotation(Authorization.class);
        if (authAnnotation != null) {
            /*
             * Get token from the request header, then validating the token
             * through token manager
             */
            String token = request.getParameter("token");
          
            if (token != null && tokenService.authToken(token)) {
                String openId = tokenService.getOpenId(token);
                UserBase user = userRepository.findByOpenId(openId); /* acquire the user from repository */

                /*
                 * Validate user type if required, returning permission denied error when failed.
                 */
                if (authAnnotation.userLimited() && user.getType() != authAnnotation.userType()) {
                    String resp = ResponseWrapper.wrapResponse(commonError.E_PERMISSION_DENIED, null);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getOutputStream().print(resp);
                    return false;
                }
                
                /* succeeded. Store the openId, corresponding to the token, to the request for later injection. */
                request.setAttribute("currentUser", user);
                return true;
            }
          
            /*
             * Return HTTP 401 error with common error message as the authorization
             * of token has been failed.
             */
            String resp = ResponseWrapper.wrapResponse(commonError.E_TOKEN_AUTH, null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().print(resp);
            return false;
        }
        return true;
    }
}

/** @file
 * Filter for parameter resolver, for methods that is annotated with @RoutineResolver
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

import com.autosig.annotation.RoutineResolver;
import com.autosig.error.commonError;
import com.autosig.service.RoutineService;
import com.autosig.util.ResponseWrapper;

/**
 * Customed Authorization Interceptor, witch resolve group, activity or task.
 */
@Component
public class RoutineResolverInterceptor implements HandlerInterceptor {

    @Autowired
    private RoutineService routineService;
    
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        /* Passing when not a handler method */
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        
        /* do filter only when the method is annotated with @Authorization. */
        RoutineResolver routAnnotation = method.getAnnotation(RoutineResolver.class);
        if (routAnnotation != null) {
            String uid = request.getParameter("uid");
            commonError error = commonError.E_FAULT;
            Object routine = null;
            
            switch(routAnnotation.type()) {
            case GROUP:
                routine = routineService.getGroupByUid(uid);
                error = commonError.E_GROUP_NON_EXISTING;
                break;
            case ACTIVITY:
                routine = routineService.getActivityByUid(uid);
                error = commonError.E_ACTIVITY_NON_EXISTING;
                break;
            case TASK:
                routine = routineService.getTaskByUid(uid);
                error = commonError.E_TASK_NON_EXISTING;
                break;
            }
            
            if (routine == null) {
                String resp = ResponseWrapper.wrapResponse(error, null);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getOutputStream().print(resp);
                return false;
            }
            
            /* succeeded. Store the routine instance, corresponding to the token, to the request for later injection. */
            request.setAttribute("currentRoutine", routine);
            return true;
            
        }
        return true;
    }
}

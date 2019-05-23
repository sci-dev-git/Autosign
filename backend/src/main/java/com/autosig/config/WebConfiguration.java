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

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Autowired
    private CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver;
    @Autowired
    private CurrentGroupMethodArgumentResolver currentGroupMethodArgumentResolver;
    @Autowired
    private CurrentActivityMethodArgumentResolver currentAcitivityMethodArgumentResolver;
    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;
    @Autowired
    private RoutineResolverInterceptor routineResolverInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/**"); /* Filter all the request, checking request method annotated with @Authorization */
        registry.addInterceptor(routineResolverInterceptor)
                .addPathPatterns("/**");
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserMethodArgumentResolver);
        argumentResolvers.add(currentGroupMethodArgumentResolver);
        argumentResolvers.add(currentAcitivityMethodArgumentResolver);
    }
}

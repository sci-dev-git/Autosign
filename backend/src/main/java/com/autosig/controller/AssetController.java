/** @file
 * Controller for /group/* APIs
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
package com.autosig.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autosig.error.commonError;
import com.autosig.util.Constants;
import com.autosig.util.ResponseWrapper;

@RestController
public class AssetController {
    /**
     * API for getting binary stream of assets.
     * @param path Path of target asset.
     * @return
     */
    @RequestMapping(value = "/asset/get.do", method = RequestMethod.GET)
    public @ResponseBody void get(@RequestParam(value="path") String path,
            HttpServletResponse response) {
        
        response.setContentType("image/png");
        
        try {
            ServletOutputStream out = response.getOutputStream();
            
            try {
                File file = new File("./assets/" + path);
                FileInputStream in = new FileInputStream(file);
                byte[] b = null;
                while(in.available() > 0) {
                    if(in.available() > Constants.CHUNK_SIZE) {
                        b = new byte[Constants.CHUNK_SIZE];
                    } else {
                        b = new byte[in.available()];
                    }
                    in.read(b, 0, b.length);
                    out.write(b, 0, b.length);
                }
                in.close();
            } catch(Exception exp) {
                String resp = ResponseWrapper.wrapResponse(commonError.E_ASSET_NOT_FOUND, null);
                response.setContentType("application/json");
                out.print(resp);
            }
            out.flush();
            out.close();
            response.setStatus(200);
            
        } catch(Exception exp) {
            response.setStatus(404);
        }
    }
}

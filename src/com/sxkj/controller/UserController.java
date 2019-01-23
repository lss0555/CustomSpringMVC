package com.sxkj.controller;

import com.sxkj.annotation.CustomAutowired;
import com.sxkj.annotation.CustomController;
import com.sxkj.annotation.CustomRequstMapping;
import com.sxkj.annotation.CustomRequstParam;
import com.sxkj.service.UserServiceInter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description 用户controller
 * @Author lss0555
 * @Date 2019/1/22/022 17:04
 **/
@CustomController
@CustomRequstMapping("/test")
public class UserController {
    @CustomAutowired(value = "UserServiceImpl")
    UserServiceInter userServiceInter;

    @CustomRequstMapping("/getName")
    public void getUserName(HttpServletResponse response,@CustomRequstParam("id") String id) throws IOException {
        String userName = userServiceInter.getUserName(id);
        PrintWriter writer = response.getWriter();
        writer.write(userName);
    }
}

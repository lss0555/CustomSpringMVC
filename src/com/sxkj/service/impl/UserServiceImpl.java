package com.sxkj.service.impl;

import com.sxkj.annotation.CustomService;
import com.sxkj.service.UserServiceInter;

/**
 * @Description 用户实现类
 * @Author lss0555
 * @Date 2019/1/22/022 17:00
 **/
@CustomService(value = "UserServiceImpl")
public class UserServiceImpl implements UserServiceInter {
    @Override
    public String getUserName(String id) {
        System.out.println("用户Id:"+id);
        return "hello,id:"+id;
    }
}

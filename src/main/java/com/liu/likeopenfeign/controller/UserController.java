package com.liu.likeopenfeign.controller;

import com.liu.likeopenfeign.core.ProxyService;
import com.liu.likeopenfeign.core.domain.CommonData;
import com.liu.likeopenfeign.core.domain.User;
import com.liu.likeopenfeign.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController() {
        userService = new ProxyService().getProxy(UserService.class);
    }

    @GetMapping("/")
    public List<User> findAll(){
        List<User> users = userService.findAll().getData();
        users.forEach(System.out::println);
        return users;
    }
}

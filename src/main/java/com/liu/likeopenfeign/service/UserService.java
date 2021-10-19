package com.liu.likeopenfeign.service;

import com.liu.likeopenfeign.core.FeignService;
import com.liu.likeopenfeign.core.domain.CommonData;
import com.liu.likeopenfeign.core.domain.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignService(hostname = "http://127.0.0.1", port = 80)
public interface UserService {
    @GetMapping("/user/")
    CommonData<List<User>> findAll();
    @GetMapping("/user/{id}")
    CommonData<User> findUserById(@PathVariable("id") int id);
    @PostMapping("/user/")
    void saveUser(@RequestBody User user);
    @DeleteMapping("/user/{id}")
    void deleteUserById(@PathVariable("id") int id);
    @PutMapping("/user/")
    void updateUser(@RequestBody User user);
}

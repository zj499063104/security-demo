package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.domain.User;

public interface UserService extends IService<User> {

    void saveUserDetails(User user);
}

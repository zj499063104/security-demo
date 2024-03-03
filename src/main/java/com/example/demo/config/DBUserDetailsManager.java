package com.example.demo.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
@Component
public class DBUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<com.example.demo.domain.User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        com.example.demo.domain.User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        } else {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(()->"USER_LIST");//权限
            authorities.add(()->"USER_ADD");//权限
            authorities.add(()->"ROLE_ADMIN");//角色  ps:角色和权限用同一个属性存储，用户拥有"ADMIN"角色，设置时要在前面加上"ROLE_"，用于区分角色
            return new User(user.getUsername()
                    , user.getPassword()
                    , user.getEnabled()
                    , true //user.isAccountNonExpired() //用户账号是否未过期
                    , true //user.isCredentialsNonExpired() //用户凭证是否未过期
                    , true //user.isAccountNonLocked() //用户是否未被锁定
                    , authorities //user.getAuthorities() //权限列表
            );
        }
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        return null;
    }

    @Override
    public void createUser(UserDetails userDetails) {
        com.example.demo.domain.User user = new com.example.demo.domain.User();
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setEnabled(true);
        userMapper.insert(user);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }


}

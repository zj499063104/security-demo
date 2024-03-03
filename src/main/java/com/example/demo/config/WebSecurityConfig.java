package com.example.demo.config;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.HashMap;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity//开启方法授权
//@EnableWebSecurity //Spring项目总需要添加此注解，SpringBoot项目中不需要
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //authorizeRequests()：开启授权保护
        http.authorizeRequests(authorize ->
                authorize
                    /*//具有USER_LIST权限的用户可以访问/user/list
                    .requestMatchers("user/list").hasAuthority("USER_LIST")
                    //具有USER_ADD权限的用户可以访问/user/add
                    .requestMatchers("/user/add").hasAuthority("USER_ADD")*/
                    //具有管理员角色的用户可以访问/user/**
//                    .requestMatchers("/user/**").hasRole("ADMIN")
                    //anyRequest()：对所有请求开启授权保护
                    .anyRequest()
                    //authenticated()：已认证请求会自动被授权
                    .authenticated()
                )
            .formLogin(withDefaults())//表单授权方式
            .httpBasic(withDefaults());//基本授权方式
        http.csrf((csrf)->{csrf.disable();});//关闭csrf攻击防御

        //使用表单授权方式
        http.formLogin(form ->{
            form.loginPage("/login").permitAll() //登录页面无需授权即可访问
                .usernameParameter("username") //自定义表单用户名参数，默认是username
                .passwordParameter("password") //自定义表单密码参数，默认是password
                .failureUrl("/login?error")//登录失败的返回地址
                .successHandler(new MyAuthenticationSuccessHandler()) //认证成功时的处理
                .failureHandler(new MyAuthenticationFailureHandler()) //认证失败时的处理
            ;
        });

        http.logout(logout ->{
            logout.logoutSuccessHandler(new MyLogoutSuccessHandler())  //注销成功时的处理
            ;
        });


        http.exceptionHandling(exception ->{
            exception.authenticationEntryPoint(new MyAuthenticationEntryPoint());//请求未认证的接口
            exception.accessDeniedHandler(new AccessDeniedHandler() {//请求未授权的接口
                @Override
                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                    //创建结果对象
                    HashMap result = new HashMap();
                    result.put("code", -1);
                    result.put("message", "没有权限");

                    //转换成json字符串
                    String json = JSON.toJSONString(result);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().println(json);
                }
            });
        });

        http.cors(withDefaults());//跨域

        //会话管理
        http.sessionManagement(session ->{
            session.maximumSessions(1)
                    .expiredSessionStrategy(new MySessionInformationExpiredStrategy());
        });
        return http.build();
    }

    /**
     * 通过数据操作用户名和密码
     * todo//用这种@Bean的方法把对象交给spring管理，失败了。不知道为什么，猜测跟mybatis-plus有关。
     * todo//先用@Component把对象交给spring管理。
     */
    /*@Bean
    public UserDetailsService userDetailsService() {
        DBUserDetailsManager manager = new DBUserDetailsManager();
        return manager;
    }*/


    /**
     * 通过内存操作用户名和密码
     * @return
     */
    /*@Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(//此行设置断点可以查看创建的user对象

                User.withDefaultPasswordEncoder()
                        .username("huan")//自定义用户名
                        .password("123456")//自定义密码
                        .roles("USER")//自定义角色
                        .build()
        );
        return inMemoryUserDetailsManager;
    }*/
}

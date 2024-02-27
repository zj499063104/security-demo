package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

@SpringBootTest
class SecurityDemoApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	public void testPasswordEncoder(){
		// 工作因子，默认值是10，最小值是4，最大值是31，值越大运算速度越慢
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
		//明文："password"
		//密文：result，即使明文密码相同，每次生成的密文也不一致
		String result = bCryptPasswordEncoder.encode("password");
		//密码校验
		Assert.isTrue(bCryptPasswordEncoder.matches("password",result),"密码不一致");;
	}

}

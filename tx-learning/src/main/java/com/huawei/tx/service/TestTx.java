package com.huawei.tx.service;

import com.huawei.tx.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xixi
 * @Description： 测试添加了 @Transactional 注解的方法，使用 private、默认、protected 都会导致事务失效
 * @create 2020/6/5
 * @since 1.0.0
 */
public class TestTx {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ac = new
				AnnotationConfigApplicationContext(AppConfig.class);
		UserService bean = ac.getBean(UserService.class);
		bean.laoda("tom", "jerry", 100);
	}
}

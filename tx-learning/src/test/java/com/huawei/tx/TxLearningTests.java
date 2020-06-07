package com.huawei.tx;

import com.huawei.tx.config.AppConfig;
import com.huawei.tx.service.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author xixi
 * @Description： 测试类
 * @create 2020/6/3
 * @since 1.0.0
 */
public class TxLearningTests {

	//***************** 基于XML配置
//	@Test
//	public void test01() {
//		ApplicationContext ac = new
//				ClassPathXmlApplicationContext("applicationContext.xml");
//		UserService bean = ac.getBean(UserService.class);
//		bean.transferNoTransactionManage("tom", "jerry", 100);
//	}

//	@Test
//	public void test02() {
//		ApplicationContext ac = new
//				ClassPathXmlApplicationContext("applicationContext.xml");
//		UserService bean = ac.getBean(UserService.class);
//		bean.transferXMLTransactionManage("tom", "jerry", 100);
//	}

	//***************** 基于配置类与注解
//	@Test
//	public void test01_1() {
//		AnnotationConfigApplicationContext ac = new
//				AnnotationConfigApplicationContext(AppConfig.class);
//		UserService bean = ac.getBean(UserService.class);
//		// 说明：没有Spring事务管理，每个Dao就是一个事务（数据库内部事务机制），发生异常时，只回滚异常之前的事务
////		bean.transferNoTransactionManage("tom", "jerry", 100);
//
//		bean.transferProgrammaticTransactionManage("tom", "jerry", 100);
//
//	}

	@Test
	public void test01_2() {
		AnnotationConfigApplicationContext ac = new
				AnnotationConfigApplicationContext(AppConfig.class);
		UserService bean = ac.getBean(UserService.class);
		bean.laoda("tom", "jerry", 100);
	}
}

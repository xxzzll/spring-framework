package com.huawei.tx.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author xixi
 * @Description： 主配置类
 * @create 2020/6/3
 * @since 1.0.0
 */
@Configuration // 配置类 <==> XML
@EnableTransactionManagement // 开启使用注解管理aop事务
@ComponentScan(value = "com.huawei.tx") // 包扫描
@EnableAspectJAutoProxy(exposeProxy=true)
public class AppConfig {

	public AppConfig(){
		System.out.println("create---{{AppConfig}}");
	}

	/**
	 *
	 * @Import(AspectJAutoProxyRegistrar.class)
	 * public @interface EnableAspectJAutoProxy {
	 *   // ....
	 * }
	 *	@EnableAspectJAutoProxy 表示开启 AOP 代理自动配置，如果配 @EnableAspectJAutoProxy 表示使用 CGLIB 进行代理对象的生成；
	 *	设置 @EnableAspectJAutoProxy(exposeProxy=true) 表示通过 AOP 框架暴露该代理对象，
	 *	aopContext 能够访问.
	 *
	 * 从 @EnableAspectJAutoProxy 的定义可以看得出，它引入 AspectJAutoProxyRegistrar 对象，
	 * 该对象是基于注解 @EnableAspectJAutoProxy 注册一个 AnnotationAwareAspectJAutoProxyCreator，
	 * 该对象通过调用 AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);
	 * 注册一个aop代理对象生成器。
	 *
	 * 原文链接：https://blog.csdn.net/pml18710973036/java/article/details/61654277
	 *
	 *==================================================================================
	 *
	 * 判断一个Bean是否是AOP代理对象可以使用如下三种方法：
	 *
	 * 	1. AopUtils.isAopProxy(bean) ： 是否是代理对象；
	 *
	 * 	2. AopUtils.isCglibProxy(bean) ： 是否是CGLIB方式的代理对象；
	 *
	 * 	3. AopUtils.isJdkDynamicProxy(bean) ： 是否是JDK动态代理方式的代理对象；
	 *
	 * 	原文链接：https://blog.csdn.net/dapinxiaohuo/article/details/52092447
	 *
	 */

	@Bean
	public TransactionTemplate transactionTemplate(DataSourceTransactionManager transactionManager) {
		TransactionTemplate transactionTemplate = new TransactionTemplate();
		transactionTemplate.setTransactionManager(transactionManager);
		return transactionTemplate;

	}

	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		System.out.println("@Bean 已经运行");
		DataSourceTransactionManager transactionManager = new
				DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;

	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSource);

		return jdbcTemplate;
	}

	@Bean
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mysql://localhost:3306/spring_tx?useSSL=false");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername("root");
		dataSource.setPassword("Lzx@123456");

		return dataSource;
	}
}

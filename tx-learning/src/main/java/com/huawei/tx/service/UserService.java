package com.huawei.tx.service;

import com.huawei.tx.dao.UserDao;
import com.huawei.tx.dao.UserDao1;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author xixi
 * @Description： 服务类
 * @create 2020/6/3
 * @since 1.0.0
 */
@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserDao1 userDao1; // 跟UserDao唯一不同是：转出和转入操作的不是同一张表

	@Autowired
	private TransactionTemplate transactionTemplate;


	/**
	 * 沒有事務管理的转账的业务
	 *
	 * @param fromName
	 * @param toName
	 * @param money
	 */
	public void transferNoTransactionManage(String fromName, String toName, Integer money) {
		userDao.out(fromName, money);// 转出钱
		int x = 10;
		if (x == 10)
			throw new RuntimeException("出错啦!");
		userDao.in(toName, money);// 收入钱
	}

	/**
	 * 编程式事務的转账的业务
	 *
	 * @param fromName
	 * @param toName
	 * @param money
	 */
	public void transferProgrammaticTransactionManage(String fromName, String toName, Integer money) {
		transactionTemplate.execute(status -> {
			userDao.out(fromName, money);// 转出钱
			int x = 10;
			if (x == 10)
				throw new RuntimeException("出错啦!");
			userDao.in(toName, money);// 收入钱
			return null;
		});

	}

	/**
	 * 基于XML配置事務的转账的业务
	 *
	 * @param fromName
	 * @param toName
	 * @param money
	 */
	public void transferXMLTransactionManage(String fromName, String toName, Integer money) {
		userDao.out(fromName, money);// 转出钱
		int x = 10;
		if (x == 10)
			throw new RuntimeException("出错啦!");
		userDao.in(toName, money);// 收入钱

	}


	/**
	 * 声明式事務管理的转账的业务
	 *
	 * @param fromName
	 * @param toName
	 * @param money
	 */
	@Transactional
	public void transferDeclarativeTransactionManage(String fromName, String toName, Integer money) {
		userDao.out(fromName, money);// 转出钱
		int x = 10;
		if (x == 10)
			throw new RuntimeException("出错啦!");
		userDao.in(toName, money);// 收入钱
	}



	/**
	 * 在以下测试代码中，方法调用时，使用 try{ }catch(...){ } 防止被调用方法：xiaodi() 抛出的异常对调用者：laoda() 产生影响
	 *--------------------------------------------------------------------------------------------------------
	 * 七种事务传播特性总结：
	 * 1. Propagation.NEVER : laoda() 没事务 xiaodi()就以非事务执行, laoda() 有事务,xiaodi() 执行前就抛出异常(标注了Propagation.NEVER方法内抛出异常)
	 * 2. Propagation.NOT_SUPPORTED，不支持事务，若 laoda() 有事务就挂起 laoda() 事务(锁表)再执行 xiaodi();
	 * 3. Propagation.SUPPORTS，laoda() 有事务就加入当前事务，laoda() 没事务就算了，若 laoda() 有事务，互为影响;
	 * 4. Propagation.REQUIRES_NEW：不管 laoda() 有没有事务，xiaodi() 都会开一个事务; 如 laoda() 有事务，则两个事务互不影响;
	 * 5. Propagation.NESTED 嵌套事务，异常不会影响 laoda() 事务，反之 laoda() 异常会影响 xiaodi() 事务;
	 * 6. Propagation.REQUIRED，laoda() 有事务就合并使用，laoda()没有事务 xiaodi() 就自己新建事务，反正自己一定要有事务;
	 * 7. Propagation.MANDATORY，强制 laoda() 必须有事务，否则 xiaodi() 方法执行前会抛出异常，laoda() 有事务就使用其事务;
	 * -------------------------------------------------------------------------------------------------------------
	 */


	/**
	 * 测试结果：
	 * 	1. 若 laoda()方法有事务管理，xiaodi() 方法不执行，直接报错;
	 * 	2. 若 laoda()方法无事务管理，xiaodi() 方法正常执行;
	 * 	3. 间接证明通过代理对象 ((UserService)AopContext.currentProxy()).xiaodi(fromName, toName, money);
	 * 	   调用同一个 service 内的方法，事务不会失效
	 */
	// 1. Propagation.NEVER : laoda() 没事务 xiaodi()就以非事务执行, laoda() 有事务,xiaodi() 执行前就抛出异常(标注了Propagation.NEVER方法内抛出异常)
//	@Transactional
//	public void laoda(String fromName, String toName, Integer money) {
//		userDao.out(fromName, money);// 转出钱
//
//		try {
//			((UserService)AopContext.currentProxy()).xiaodi(fromName, toName, money);
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//		}
//
//		int x = 9;
//		if (x == 10)
//			throw new RuntimeException("老大出错啦!");
//	}
//	@Transactional(propagation = Propagation.NEVER)
//	public void xiaodi(String fromName, String toName, Integer money) {
//		userDao.in(toName, money);// 收入钱
//		int x = 10;
//		if (x == 10)
//			throw new RuntimeException("小弟出错啦!");
//
//	}
	//*************************************************************************************************************

	/**
	 * 结论：
	 * 	1. 若 laoda() 使用事务，xiaodi() 方法就会先挂起(锁表)在执行，
	 * 		1)操作相同的表，
	 * 			则会发生死锁，抛出的异常为：org.springframework.dao.CannotAcquireLockException/Lock wait timeout exceeded; try restarting transaction;
	 * 		2)操作不相同的表
	 * 			当 xiaodi() 方法抛出异常，laoda() 和 xiaodi() 方法都不会回滚;
	 * 			当 laoda() 方法抛出异常，laoda() 方法会回滚，xiaodi() 方法不会回滚;
	 */
	// 2. Propagation.NOT_SUPPORTED，不支持事务，若 laoda() 有事务就挂起 laoda() 事务(锁表)再执行 xiaodi()
//	@Transactional
//	public void laoda(String fromName, String toName, Integer money) {
//		userDao1.out(fromName, money);// 转出钱
//
//		try {
//			((UserService)AopContext.currentProxy()).xiaodi(fromName, toName, money);
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//		}
//
//		int x = 10;
//		if (x == 10)
//			throw new RuntimeException("老大出错啦!");
//	}
//	@Transactional(propagation = Propagation.NOT_SUPPORTED)
//	public void xiaodi(String fromName, String toName, Integer money) {
//		userDao1.in(toName, money);// 收入钱
//		int x = 9;
//		if (x == 10)
//			throw new RuntimeException("小弟出错啦!");
//
//	}
	//*************************************************************************************************************//

	/**
	 * 结论：
	 * 	1. 若 laoda() 有事务，xiaodi() 方法就加入的同一事务中;
	 * 		当 xiaodi() 方法抛异常，laoda() 和 xiaodi() 方法都回滚;
	 * 		当 laoda() 方法抛异常，laoda() 和 xiaodi() 方法也都回滚;
	 *
	 * 	2. 若 laoda() 没事务，xiaodi() 方法也不会创建事务;
	 * 		两者无论谁抛出异常都不会回滚;
	 */
	// 3. Propagation.SUPPORTS，laoda() 有事务就加入当前事务，laoda() 没事务就算了，若 laoda() 有事务，互为影响;
//	@Transactional
//	public void laoda(String fromName, String toName, Integer money) {
//		userDao.out(fromName, money);// 转出钱
//
//		try {
//			((UserService)AopContext.currentProxy()).xiaodi(fromName, toName, money);
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//		}
//
//		int x = 10;
//		if (x == 10)
//			throw new RuntimeException("老大出错啦!");
//	}
//	@Transactional(propagation = Propagation.SUPPORTS)
//	public void xiaodi(String fromName, String toName, Integer money) {
//		userDao.in(toName, money);// 收入钱
//		int x = 9;
//		if (x == 10)
//			throw new RuntimeException("小弟出错啦!");
//
//	}

	//*************************************************************************************************************//

	/**
	 * 结论：
	 * 	1. 若 laoda() 方法有事务，xiaodi() 方法执行前会挂其 laoda() 方法的事务(锁表)，开启自己的事务执行，
	 * 		1）操作相同的表就会出现死锁;
	 * 		2）操作不同的表
	 * 			当 xiaodi() 方法抛出异常，结果： laoda() 方法不会回滚;
	 * 			当 laoda() 方法抛出异常，结果： xiaodi() 方法不会回滚;
	 */
	// 4. Propagation.REQUIRES_NEW：不管 laoda() 有没有事务，xiaodi() 都会开一个事务; 如 laoda() 有事务，则两个事务互不影响;
//	@Transactional
//	public void laoda(String fromName, String toName, Integer money) {
//		userDao1.out(fromName, money);// 转出钱
//
//		try {
//			((UserService)AopContext.currentProxy()).xiaodi(fromName, toName, money);
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//		}
//
//		int x = 10;
//		if (x == 10)
//			throw new RuntimeException("老大出错啦!");
//	}
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void xiaodi(String fromName, String toName, Integer money) {
//		userDao1.in(toName, money);// 收入钱
//		int x = 9;
//		if (x == 10)
//			throw new RuntimeException("小弟出错啦!");
//
//	}
	//*************************************************************************************************************//

	/**
	 * 结论：
	 * 	1.若 laoda() 方法有事务
	 * 		当 xiaodi() 方法有异常，laoda() 方法不会回滚;
	 * 		当 laoda() 方法有异常，xiaodi() 方法也会回滚;
	 *
	 * 	2.若 laoda() 方法没有事务
	 * 		当 laoda() 方法有异常，xiaodi() 方法不会回滚;
	 * 		当 xiaodi() 方法有异常，xiaodi() 方法会回滚;
	 *
	 */
	// 5. Propagation.NESTED 嵌套事务，异常不会影响 laoda() 事务，反之 laoda() 异常会影响 xiaodi() 事务;
//	@Transactional
//	public void laoda(String fromName, String toName, Integer money) {
//		userDao.out(fromName, money);// 转出钱
//
//		try {
//			((UserService)AopContext.currentProxy()).xiaodi(fromName, toName, money);
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//		}
//
//		int x = 9;
//		if (x == 10)
//			throw new RuntimeException("老大出错啦!");
//	}
//	@Transactional(propagation = Propagation.NESTED)
//	public void xiaodi(String fromName, String toName, Integer money) {
//		userDao.in(toName, money);// 收入钱
//		int x = 10;
//		if (x == 10)
//			throw new RuntimeException("小弟出错啦!");
//
//	}
	//*************************************************************************************************************//
	/**
	 *
	 * 结论：
	 *  1. 若 laoda() 有事务
	 *     	当 xiaodi() 方法有异常，laoda() 方法也会回滚;
	 *     	当 laoda() 方法有异常，xiaodi() 方法也会回滚;
	 */
	// 6. Propagation.REQUIRED，laoda() 有事务就合并使用，laoda()没有事务 xiaodi() 就自己新建事务，反正自己一定要有事务;
//	@Transactional
//	public void laoda(String fromName, String toName, Integer money) {
//		userDao.out(fromName, money);// 转出钱
//
//		try {
//			((UserService)AopContext.currentProxy()).xiaodi(fromName, toName, money);
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//		}
//
//		int x = 9;
//		if (x == 10)
//			throw new RuntimeException("老大出错啦!");
//	}
//	@Transactional(propagation = Propagation.REQUIRED)
//	public void xiaodi(String fromName, String toName, Integer money) {
//		userDao.in(toName, money);// 收入钱
//		int x = 10;
//		if (x == 10)
//			throw new RuntimeException("小弟出错啦!");
//
//	}

	//*************************************************************************************************************//
	// 7. Propagation.MANDATORY，强制 laoda() 必须有事务，否则 xiaodi() 方法执行前会抛出异常，laoda() 有事务就使用其事务;
//	@Transactional
	public void laoda(String fromName, String toName, Integer money) {
		userDao1.out(fromName, money);// 转出钱

		try {
			((UserService)AopContext.currentProxy()).xiaodi(fromName, toName, money);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		int x = 10;
		if (x == 10)
			throw new RuntimeException("老大出错啦!");
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void xiaodi(String fromName, String toName, Integer money) {
		userDao1.in(toName, money);// 收入钱
		int x = 10;
		if (x == 10)
			throw new RuntimeException("小弟出错啦!");

	}

}

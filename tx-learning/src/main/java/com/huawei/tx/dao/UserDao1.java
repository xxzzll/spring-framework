package com.huawei.tx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author xixi
 * @Description：
 * @create 2020/6/3
 * @since 1.0.0
 */
@Repository
public class UserDao1 {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 转出(t_user)
	 *
	 * @param fromName
	 * @param money
	 */
	public void out(String fromName, Integer money) {
		String sql = "update t_user set money = money-? where name =? ";
		int i = jdbcTemplate.update(sql, money, fromName);
		System.out.println("out方法更新条数：" + i);
	}

	/**
	 * 转入(t_user_copy)
	 *
	 * @param toName
	 * @param money
	 */
	public void in(String toName, Integer money) {
		String sql = "update t_user_copy set money = money+? where name =? ";
		int i = jdbcTemplate.update(sql, money, toName);
		System.out.println("in方法更新条数：" + i);
	}
}

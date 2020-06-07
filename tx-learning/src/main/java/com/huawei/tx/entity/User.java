package com.huawei.tx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author xixi
 * @Description： t_user 表映射实体类
 * @create 2020/6/3
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;

	private String name;

	private int money;
}

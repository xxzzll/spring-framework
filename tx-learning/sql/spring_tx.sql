create database spring_tx  CHARSET=utf8mb4;

use spring_tx;

create table t_user
(
    id    int auto_increment comment 'ID' primary key,
    name  varchar(64) comment '名称',
    money int comment '账户钱数'
) comment '用户表' engine = InnoDB
                default charset = utf8mb4;

insert into t_user(name, money) values ('Tom', 1000),('jerry', 1000);

select * from t_user;

create table t_user_copy like t_user;

insert into t_user_copy select * from t_user;

select * from t_user_copy;
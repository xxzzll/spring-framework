# 						Spring 事务

## 事务概念

事务:

​	是数据库操作的最小工作单元,是作为单个逻辑工作单元执行的一系列操作;这些操作作为一个整体一起向系
统提交,要么都执行、要么都不执行;事务是一组不可再分割的操作集合(工作逻辑单元);
通俗点说就是为了达到某个目的而做的一系列的操作要么一起成功(事务提交),要么一起失败(事务回滚)。

最常见的例子就转账:
小明给如花转账:
开启事务--------------
1从小明的账户扣除1000块
2给如花的账户增加1000块
事务提交-------------
上面例子的任何步骤一旦出现问题,都会导致事务回滚。
从搭讪到结婚就是事务提交。 女方要求男方重新追求她一次就是事务回滚。



## 事务四大特性(一原持久隔离)

1、 原子性(Atomicity)

​	事务中所有操作是不可再分割的原子单位。事务中所有操作要么全部执行成功,要么全部执行失败。 

2、 一致性(Consistency)

​	事务执行后,数据库状态与其它业务规则保持一致。如转账业务,无论事务执行成功与否,参与转账的两个账号余额之和应该是不变的。

 3、 隔离性(Isolation)

​	隔离性是指在并发操作中,不同事务之间应该隔离开来,使每个并发中的事务不会相互干扰。

 4、 持久性(Durability)

​	一旦事务提交成功,事务中所有的数据操作都必须被持久化到数据库中,即使提交事务后,数据库马上崩溃,在数据库重启时,也必须能保证通过某种机制恢复数据。



## 原生JDBC事务处理

```java
try{
	connection.setAutoCommit( false);
	// 数据库操作...
	// todo insert/update operate
	connection.commit();
}catch(Exception ex){
	connection.rollback();
}finally{
	connection.setAutoCommit( true);
}
```



## 事务隔离级别

```reStructuredText
数据库事务的隔离级别有4种,由低到高分别为Read uncommitted 、Read committed 、Repeatable read 、
Serializable ;而且,在事务的并发操作中可能会出现脏读,不可重复读,幻读,事务丢失。
```



### 脏读:

```reStructuredText
读取了未提交的新事务,然后被回滚了。
​事务A读取了事务B中尚未提交的数据。如果事务B回滚,则A读取使用了错误的数据。
```



### 不可重复读

```reStructuredText
读取了提交的新事务,指更新操作。
​不可重复读是指在对于数据库中的某个数据,一个事务范围内多次查询却返回了不同的数据值,这是由于在查询间
隔,被另一个事务修改并提交了。
```



### 幻读

```reStructuredText
也是读取了提交的新事务,指增删操作。
​在事务A多次读取构成中,事务B对数据进行了新增操作,导致事务A多次读取的数据不一致。
```



### 第一类事物丢失(回滚丢失)

```reStructuredText
对于第一类事物丢失,就是比如A和B同时在执行一个数据,然后B事物已经提交了,然后A事物回滚了,这样B事物
的操作就因A事物回滚而丢失了。
```



### 第二类事物丢失(提交覆盖丢失)

```reStructuredText
对于第二类事物丢失,也称为覆盖丢失,就是A和B一起执行一个数据,两个同时取到一个数据,然后B事物首先提
交,但是A事物加下来又提交,这样就覆盖了B事物。
```



### Read uncommitted

```reStructuredText
读未提交,顾名思义,就是一个事务可以读取另一个未提交事务的数据。 会产生脏读。
```



### Read committed

```reStructuredText
读提交,顾名思义,就是一个事务要等另一个事务提交后才能读取数据。 会产生不可重复读。
```



### Repeatable read

```reStructuredText
重复读,就是在开始读取数据(事务开启)时,不再允许修改操作。 可能会产生幻读。
```



### Serializable

```reStructuredText
Serializable 是最高的事务隔离级别,在该级别下,事务串行化顺序执行,可以避免脏读、不可重复读与幻读。但是
这种事务隔离级别效率低下,比较耗数据库性能,一般不使用。
```



==大多数数据库默认的事务隔离级别是Read committed,比如Sql Server , Oracle。Mysql的默认隔离级别是Repeatable read。==



## 事务传播特性

### 什么是事务的传播特性

​	指的就是当一个事务方法被另一个事务方法调用时,这个事务方法应该如何进行。

### 7种事务隔离级别

1. PROPAGATION_REQUIRED

   默认事务类型,如果没有,就新建一个事务;如果有,就加入当前事务。适合绝大
   多数情况。

2. PROPAGATION_REQUIRES_NEW

   如果没有,就新建一个事务;如果有,就将当前==事务挂起==。

3. PROPAGATION_NESTED

   如果没有,就新建一个事务;如果有,就在当前事务中==嵌套其他事务==。

4. PROPAGATION_SUPPORTS

   如果没有,就以非事务方式执行;如果有,就使用当前事务。

5. PROPAGATION_NOT_SUPPORTED

   如果没有,就以非事务方式执行;如果有,就将当前==事务挂起==。即无论如何
   不支持事务。

6. PROPAGATION_NEVER

   如果没有,就以非事务方式执行;如果有,就抛出异常。

7. PROPAGATION_MANDATORY

   如果没有,就抛出异常;如果有,就使用当前事务。

### 规律总结

#### 死活不要事务

- PROPAGATION_NEVER:没有就非事务执行,有就抛出异常
- PROPAGATION_NOT_SUPPORTED:没有就非事务执行,有就直接挂起,然后非事务执行

#### 可有可无

- PROPAGATION_SUPPORTS: 有就用,没有就算了

#### 必须有事务

- PROPAGATION_REQUIRES_NEW:有没有都新建事务,如果原来有,就将原来的挂起。
- PROPAGATION_NESTED: 如果没有,就新建一个事务;如果有,就在当前事务中嵌套其他事务。
- PROPAGATION_REQUIRED: 如果没有,就新建一个事务;如果有,就加入当前事务
- PROPAGATION_MANDATORY: 如果没有,就抛出异常;如果有,就使用当前事务。



## 怎么配置事务









## Spring 事务原理

```
适配器：BeanFactoryTransactionAttributeSourceAdvisor（Advisor）
通知：TransactionInterceptor（Advice）
从带有@Transaction注解的方法中，解析出TransactionAttribute：TransactionAttributeSource(TransactionAttribute)
后置处理器，生成代理对象：InfrastructureAdvisorAutoProxyCreator
```
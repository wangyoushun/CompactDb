# CompactDb

学习mybatis后造了一个轮子
介绍： 对jdbc的轻度封装，支持mysql，oracle数据库
封装了根据主键crud的操作
借鉴了mybatis功能，从配置文件读取sql，统一化管理sql
支持分页查询，封装了map集合，传参和结果集都通过map集合
支持存储过程支持事物，与spring结合时通过实现MethodInterceptor接口进行事物控制

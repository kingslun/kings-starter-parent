##初始化基础数据信息库和表结构
create database if not exists metadata;
use metadata;
# environment信息表
create table if not exists c_environment
(
    id          bigint unsigned primary key auto_increment comment '主键',
    code        varchar(20)  not null comment '环境代码',
    description varchar(200) null comment '环境描述',
    creator     varchar(20)  null comment '创建人',
    create_time datetime     not null default now() comment '创建时间',
    modifier    varchar(20)  null comment '修改人',
    modify_time datetime     not null default now() comment '创建时间',
    is_deleted  char         not null default '0' comment '逻辑删除标识 0:否 1:是'
) comment '环境信息表' charset = utf8mb4;
#初始化环境信息基础数据
insert into c_environment (id, code, description, creator, modifier)
values (100000000, 'local', '本地环境', 'lun.wang', 'lun.wang');

# kubernetes客户端信息表
create table if not exists c_kubernetes
(
    id           bigint unsigned primary key auto_increment comment '主键',
    env_id       bigint unsigned not null comment '所属环境ID',
    description  varchar(200)    null comment '集群描述信息',
    access_url   varchar(100)    not null comment '集群访问地址',
    access_token varchar(1000)   not null comment '集群访问token',
    creator      varchar(20)     null comment '创建人',
    create_time  datetime        not null default now() comment '创建时间',
    modifier     varchar(20)     null comment '修改人',
    modify_time  datetime        not null default now() comment '创建时间',
    is_deleted   char            not null default '0' comment '逻辑删除标识 0:否 1:是'
) comment 'kubernetes集群信息表' charset = utf8mb4;
#初始化k8s集群信息基础数据
insert into c_kubernetes (env_id, description, access_url, access_token, creator, modifier)
values (100000000, '本地云', 'https://localhost:6443',
        'eyJhbGciOiJSUzI1NiIsImtpZCI6ImcyMFNaRDZyUlRYd3NqaERISVctOUJYeEhNMFNpTTBqa1MxWG4zUWJaMWMifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJkZWZhdWx0LXRva2VuLTJmcjJ0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImRlZmF1bHQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJkOWE3OWQ0My1hZTA0LTRlOTMtOWI3MS1kMzBkYTMwMWIyMDYiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06ZGVmYXVsdCJ9.YW0Hx8Qa4ENG2wSP3PsSQz_8RxSb1IwRI9q6anSjU9ULRem1vNHQS3hDWXJ_Yh3Dl5yg7O9Jo0GVff_oLQV1Xv-UXtxRhEnO64zoosSUZfmO6r3poKw2Tg4Xc2yKIxnVzXrxihRfHkvIt_I5Y_c3mFsnqwHZhAvKWdlRDRUrFTyfYliyGQsl6gdIk7pAu23-2iWYunrxzxnIE452l0oM2C3uqLXldRj_TjsN_MOQ0u7DOEU_pkLTvFdcwzztEnM7HnyLGSiGuM8oY-oLMyvL2Ljq-QEtSet499zwseWX9IJJvVDdB85M3e_ppfTQWHdoQ3r19JO_ywghZzqEJyMQoQ',
        'lun.wang', 'lun.wang');

# docker账户配置表
create table if not exists c_docker_harbor
(
    id          bigint unsigned primary key auto_increment comment '主键',
    host        varchar(100) not null comment '仓库地址',
    username    varchar(20)  not null comment '登录账户',
    email       varchar(50)  not null comment '开通账户绑定的邮箱',
    password    varchar(30)  not null comment '登录密码',
    description varchar(200) null comment 'harbor描述信息',
    creator     varchar(20)  null comment '创建人',
    create_time datetime     not null default now() comment '创建时间',
    modifier    varchar(20)  null comment '修改人',
    modify_time datetime     not null default now() comment '创建时间',
    is_deleted  char         not null default '0' comment '逻辑删除标识 0:否 1:是'
) comment 'kubernetes集群信息表' charset = utf8mb4;
#初始化docker harbor信息基础数据
insert into c_docker_harbor (host, username, email, password, description, creator, modifier)
values ('kingslun', 'kingslun', 'kingslun@163.com', 'wanglun1995', '个人docker harbor私有仓库',
        'lun.wang', 'lun.wang');
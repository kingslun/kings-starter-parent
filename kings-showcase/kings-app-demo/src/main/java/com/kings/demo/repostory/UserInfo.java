package com.kings.demo.repostory;

import java.math.BigInteger;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户信息持久对象
 *
 * @author lun.wang
 * @date 2021/8/30 6:47 下午
 * @since v1.0.0
 */
@Setter
@Getter
@ToString
public class UserInfo {

  private BigInteger id;
  private String user;
  private String password;
  private String email;
  private String type;
  private String permission;
  private String creator;
  private String modifier;
  private String isDelete;
  private Date gmtCreated;
  private Date gmtModified;
}

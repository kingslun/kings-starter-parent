package com.kings.demo.repostory;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
@Entity
@Table(name = "user_info")
public class UserInfo {

  @Id
  private BigInteger id;
  private String user;
  private String password;
  private String email;
  private String type;
  private String permission;
  private String creator;
  private String modifier;
  @Column(name = "is_delete", nullable = false)
  private String isDelete;
  @Column(name = "gmt_created", nullable = false)
  private Date gmtCreated;
  @Column(name = "gmt_modified", nullable = false)
  private Date gmtModified;
}

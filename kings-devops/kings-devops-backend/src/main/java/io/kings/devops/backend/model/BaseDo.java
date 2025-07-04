package io.kings.devops.backend.model;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

/**
 * dto基类
 *
 * @author lun.wang
 * @date 2022/2/11 6:48 PM
 * @since v2.3
 */
@Getter
@Setter
@MappedSuperclass
public class BaseDo {

    @Id
    private BigInteger id;
    @Column(name = "is_deleted")
    private Character isDeleted;
    private String creator;
    @Column(name = "create_time")
    private Date createTime;
    private String modifier;
    @Column(name = "modify_time")
    private Date modifyTime;
}

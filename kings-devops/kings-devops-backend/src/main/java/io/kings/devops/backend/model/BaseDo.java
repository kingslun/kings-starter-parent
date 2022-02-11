package io.kings.devops.backend.model;

import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.Id;
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
public class BaseDo {

    @Id
    private BigInteger id;
    @Column(name = "is_delete")
    private Character isDelete;
    private String creator;
    @Column(name = "create_time")
    private String createTime;
    private String modifier;
    @Column(name = "modify_time")
    private String modifyTime;
}

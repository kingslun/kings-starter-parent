package io.kings.devops.backend.model;

import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 环境信息
 *
 * @author lun.wang
 * @date 2022/2/11 6:39 PM
 * @since v2.3
 */
@Getter
@Setter
@Entity
@ToString
@Table(name = "c_kubernetes")
public class KubernetesDo extends BaseDo {

    @Column(name = "env_id")
    private BigInteger envId;
    private BigInteger description;
    @Column(name = "access_url")
    private BigInteger accessUrl;
    @Column(name = "access_token")
    private BigInteger accessToken;
}

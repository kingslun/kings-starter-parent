package io.kings.devops.backend.dao;

import io.kings.devops.backend.model.KubernetesDo;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * docker harbor dao
 *
 * @author lun.wang
 * @date 2022/2/14 10:37 AM
 * @since v2.3
 */
public interface KubernetesDao extends CrudRepository<KubernetesDo, BigInteger> {

    @Query("from KubernetesDo where envId = (select id from EnvironmentDo where code=?1)")
    KubernetesDo findByEnvCode(String code);
}

package io.kings.devops.backend.dao;

import io.kings.devops.backend.model.EnvironmentDo;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 环境dao
 *
 * @author lun.wang
 * @date 2022/2/14 10:37 AM
 * @since v2.3
 */
public interface EnvironmentDao extends CrudRepository<EnvironmentDo, BigInteger> {

    @Query("from EnvironmentDo where code = :code")
    EnvironmentDo findByCode(String code);
}

package io.kings.devops.backend.dao;

import io.kings.devops.backend.model.DockerHarborDo;
import java.math.BigInteger;
import org.springframework.data.repository.CrudRepository;

/**
 * docker harbor dao
 *
 * @author lun.wang
 * @date 2022/2/14 10:37 AM
 * @since v2.3
 */
public interface DockerHarborDao extends CrudRepository<DockerHarborDo, BigInteger> {

}

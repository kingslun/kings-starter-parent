package com.kings.demo.repostory;

import java.math.BigInteger;
import org.springframework.data.repository.CrudRepository;

/**
 * @author lun.wang
 * @date 2021/8/30 6:58 下午
 * @since v1.0.0
 */
public interface UserInfoRepo extends CrudRepository<UserInfo, BigInteger> {

}

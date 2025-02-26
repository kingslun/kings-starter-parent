package com.kings.demo;

import com.kings.demo.repostory.UserInfo;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author lun.wang
 * @date 2021/8/30 7:06 下午
 * @since v1
 */
@Service
public class UserInfoService {

  public Optional<UserInfo> getById(BigInteger id) {
    return null;
  }
}

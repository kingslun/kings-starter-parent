package com.kings.demo;

import com.kings.demo.repostory.UserInfo;
import com.kings.demo.repostory.UserInfoRepo;
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

  private final UserInfoRepo userInfoRepo;

  public UserInfoService(UserInfoRepo userInfoRepo) {
    this.userInfoRepo = userInfoRepo;
  }

  public Optional<UserInfo> getById(BigInteger id) {
    return userInfoRepo.findById(id);
  }
}

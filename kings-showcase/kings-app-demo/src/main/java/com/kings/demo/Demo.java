package com.kings.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * 描述信息
 *
 * @author lun.wang
 * @date 2021/8/30 10:27 下午
 * @since v1.0
 */
@Slf4j
public class Demo {

  //描述信息
  private final Inner field;

  public Demo() {
    this.field = new Inner();
  }

  /**
   * 方法描述信息
   *
   * @param words 参数介绍和说明
   * @return 返回值说明
   * @throws IllegalArgumentException 异常说明
   */
  public String exec(String words) {
    if (log.isDebugEnabled()) {
      log.debug("logged xxx");
    }
    return field.exec(words);
  }

  /**
   * 对象说明 内部类使用静态类
   */
  static class Inner {

    //简短介绍
    String exec(String words) {
      Assert.hasText(words, "words is empty");
      return "hello: " + words;
    }
  }
}

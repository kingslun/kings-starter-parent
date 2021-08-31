package io.kings.framework.devops.kubernetes.sh;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * shell执行结果
 *
 * @author lun.wang
 * @date 2021/8/23 11:26 上午
 * @since v2.0
 */
@Getter
@Setter
@ToString
public class ExecResponse implements Serializable {

  /**
   * 已提交 成功与否不关注
   */
  private boolean submitted;

  /**
   * sh 正常结果集
   */
  private String successMsg;

  /**
   * sh 异常结果集
   */
  private String failureMsg;

  /**
   * 验证 正常结果集
   */
  private String validSuccessMsg;

  /**
   * 验证 异常结果集
   */
  private String validFailureMsg;
}

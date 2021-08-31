package io.kings.framework.devops.kubernetes.sh;

import java.util.concurrent.TimeUnit;

/**
 * @author lun.wang
 * @date 2021/8/23 11:55 上午
 * @since v2.0
 */
public interface KubernetesPodExecProcess {

  /**
   * 默认等待15秒 可能会有下载等耗时操作
   *
   * @return ExecResponse
   */
  default ExecResponse exec() {
    return this.exec(true);
  }

  default ExecResponse exec(boolean waitFor) {
    return this.exec(waitFor, 30, TimeUnit.SECONDS);
  }

  /**
   * k8s pod执行
   *
   * @param waitFor 是否等待结果
   * @param timeout 超时时间
   * @param unit    单位
   * @return ExecResponse
   */
  ExecResponse exec(boolean waitFor, long timeout, TimeUnit unit);
}

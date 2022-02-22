package io.kings.wechat.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信授权服务认证中心
 *
 * @author lun.wang
 * @date 2022/2/18 5:33 PM
 * @since v2.3
 */
@RestController
@SpringBootApplication
public class WeChatOauth2Application {

    @GetMapping("health")
    public String health() {
        return "Up";
    }

    @GetMapping("index")
    public String index() {
        return "hello tencent cloud1!";
    }

    public static void main(String[] args) {
        SpringApplication.run(WeChatOauth2Application.class);
    }
}

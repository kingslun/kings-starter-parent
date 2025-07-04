package io.kings.devops.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import lombok.AllArgsConstructor;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * off by two Jenkins客户端实现
 *
 * @author lun.wang
 * @date 2022/3/15 3:15 PM
 * @since v2.4
 */
@AllArgsConstructor
public class OffByTwoProvider implements ClientProvider<JenkinsServer> {

    private final String host;
    private final String username;
    private final String password;
    private JenkinsServer client;

    @Override
    public JenkinsServer provide() {
        if (this.client == null) {
            try {
                this.client = new JenkinsServer(new URI(host), username, password);
            } catch (URISyntaxException e) {
                throw new JenkinsException("jenkins host:[" + this.host + "] invalid");
            }
        }
        return this.client;
    }
}

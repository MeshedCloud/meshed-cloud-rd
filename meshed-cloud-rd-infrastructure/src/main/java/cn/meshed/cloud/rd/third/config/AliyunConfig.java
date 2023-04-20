package cn.meshed.cloud.rd.third.config;

import com.aliyun.ram20150501.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Configuration
public class AliyunConfig {

    @Value("${rd.third.aliyun.access-key-id}")
    private String accessKeyId;
    @Value("${rd.third.aliyun.access-key-secret}")
    private String accessKeySecret;

    /**
     * 使用AK&SK初始化账号Client
     *
     * @return Client
     * @throws Exception
     */
    @Bean
    public Client iamClient() throws Exception {
        Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "ram.aliyuncs.com";
        return new Client(config);
    }
}

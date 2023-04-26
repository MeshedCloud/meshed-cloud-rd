package cn.meshed.cloud.rd.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * <h1>引擎模板（原型模板）配置</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rd.basic")
public class ObjectBasicTypeProperties {

    private List<String> types;

}

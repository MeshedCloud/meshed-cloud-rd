package cn.meshed.cloud.rd.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>引擎模板（原型模板）配置</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rd.workflow.approve")
public class WorkflowProperties {

    private Boolean enable;
    private String tenantId;
    private String versionPublish;
    private String projectInitiation;

}

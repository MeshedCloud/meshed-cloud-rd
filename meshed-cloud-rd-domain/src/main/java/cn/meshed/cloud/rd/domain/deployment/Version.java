package cn.meshed.cloud.rd.domain.deployment;

import cn.meshed.cloud.rd.deployment.enums.EnvironmentEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionStatusEnum;
import cn.meshed.cloud.rd.deployment.enums.VersionTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class Version implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本编码
     */
    private Long id;

    /**
     * 版本仓库项目名称
     */
    private String name;
    /**
     * 版本实体项目全称代号
     */
    private String versionName;

    /**
     * 仓库所属项目key
     */
    private String projectKey;

    /**
     * 版本来源编码（仓库编码）
     */
    private String sourceId;

    /**
     * 版本环境
     */
    private String environments;

    /**
     * 仓库版本号
     */
    private Long version;

    /**
     * 版本类型
     */
    private VersionTypeEnum type;

    /**
     * 版本状态
     */
    private VersionStatusEnum status;

    public void setEnvironment(EnvironmentEnum environment) {
        AssertUtils.isTrue(environment != null, "环境参数不能为空");
        if (StringUtils.isNotBlank(this.environments)) {
            String[] split = this.environments.split(",");
            Set<String> envs = new HashSet<>(Arrays.asList(split));
            envs.add(String.valueOf(environment.getValue()));
            this.environments = StringUtils.join(envs.toArray(), ",");
        } else {
            this.environments = String.valueOf(environment.getValue());
        }

    }

    public void setEnvironment(String environments) {
        this.environments = environments;
    }
}

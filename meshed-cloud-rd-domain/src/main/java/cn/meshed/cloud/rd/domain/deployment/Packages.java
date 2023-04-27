package cn.meshed.cloud.rd.domain.deployment;

import cn.meshed.cloud.rd.deployment.enums.PackagesTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <h1>制品</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class Packages implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 制品名称（中文）
     */
    private String name;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 制品ID
     */
    private String artifactId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 所属项目key
     */
    private String projectKey;

    /**
     * 制品类型
     */
    private PackagesTypeEnum type;

}

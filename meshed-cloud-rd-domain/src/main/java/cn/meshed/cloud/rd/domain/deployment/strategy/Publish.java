package cn.meshed.cloud.rd.domain.deployment.strategy;

import lombok.Data;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class Publish {

    /**
     * 版本ID
     */
    private Long versionId;

    /**
     * 项目key
     */
    private String projectKey;

    /**
     * 来源ID，在客户端中来源=仓库ID
     */
    private String sourceId;

    /**
     * 发布携带信息
     */
    private String message;
}

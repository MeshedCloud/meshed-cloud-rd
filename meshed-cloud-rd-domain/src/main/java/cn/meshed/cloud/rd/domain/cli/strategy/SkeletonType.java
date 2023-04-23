package cn.meshed.cloud.rd.domain.cli.strategy;

import lombok.Getter;

/**
 * <h1>发布类型</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Getter
public enum SkeletonType {

    /**
     * 客户端 (主维度)
     */
    MAVEN,

    /**
     * 仓库模板
     */
    GIT_TEMPLATE,

}

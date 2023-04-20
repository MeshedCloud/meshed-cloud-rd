package cn.meshed.cloud.rd.domain.deployment.strategy;

import lombok.Getter;

/**
 * <h1>发布类型</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Getter
public enum PublishType {

    /**
     * 客户端 (主维度)
     */
    CLIENT,

    /**
     * 服务 (子维度)
     */
    SERVICE,

    /**
     * 模型 (子维度)
     */
    MODEL,
    /**
     * 枚举 (子维度)
     */
    ENUM,

    /**
     * 事件 (子维度)
     */
    EVENT,

    /**
     * 适配器 (粒子维度)
     */
    ADAPTER,

    /**
     * RPC (粒子维度)
     */
    RPC,

}

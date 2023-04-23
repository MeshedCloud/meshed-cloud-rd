package cn.meshed.cloud.rd.domain.cli.strategy;


import cn.meshed.cloud.rd.domain.cli.Skeleton;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface SkeletonEngine {

    /**
     * 脚手架类型
     *
     * @return SkeletonType
     */
    SkeletonType getType();

    /**
     * 构建
     *
     * @param skeleton 数据包
     */
    void build(Skeleton skeleton);
}

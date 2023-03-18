package cn.meshed.cloud.rd.domain.deployment.strategy;

/**
 * <h1>发布服务策略</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface PublishHandler<T extends Publish> {


    /**
     * 注册类型
     *
     * @return PublishType
     */
    PublishType getPublishType();

    /**
     * 发布
     *
     * @param publishData 数据包
     */
    void publish(T publishData);

}

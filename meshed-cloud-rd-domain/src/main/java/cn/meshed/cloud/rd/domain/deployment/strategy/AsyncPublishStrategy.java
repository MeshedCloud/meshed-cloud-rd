package cn.meshed.cloud.rd.domain.deployment.strategy;

import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Component
public class AsyncPublishStrategy implements BeanPostProcessor {

    private static final Map<PublishType, PublishHandler<Publish>> PUBLISH_HANDLER_MAP = new EnumMap<>(PublishType.class);

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof PublishHandler)) {
            return bean;
        }
        PublishHandler<Publish> handler = (PublishHandler) bean;
        PUBLISH_HANDLER_MAP.put(handler.getPublishType(), handler);
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    /**
     * 异步发送
     *
     * @param publishType 发布类型
     * @param publish     发布参数数据
     */
    @Async
    public Response asyncPublish(PublishType publishType, Publish publish) {
        AssertUtils.isTrue(publishType != null && publish != null, "请检查参数是否合法");
        PublishHandler<Publish> publishHandler = PUBLISH_HANDLER_MAP.get(publishType);
        if (publishHandler == null) {
            return ResultUtils.fail("策略未被实现");
        }
        publishHandler.publish(publish);
        return ResultUtils.ok();
    }

}

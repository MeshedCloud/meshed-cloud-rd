package cn.meshed.cloud.rd.domain.cli.strategy;

import cn.meshed.cloud.rd.domain.cli.Skeleton;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
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
public class AsyncSkeletonStrategy implements BeanPostProcessor {

    private static final Map<SkeletonType, SkeletonEngine> SKELETON_HANDLER_MAP = new EnumMap<>(SkeletonType.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof SkeletonEngine)) {
            return bean;
        } //非发布策略注入拦截
        SkeletonEngine handler = (SkeletonEngine) bean;
        SKELETON_HANDLER_MAP.put(handler.getType(), handler);
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    /**
     * 异步发送
     *
     * @param skeletonType 脚手架类型
     * @param skeleton     脚手架参数数据
     */
    @Async
    public Response asyncBuild(SkeletonType skeletonType, Skeleton skeleton) {
        AssertUtils.isTrue(skeletonType != null && skeleton != null, "请检查参数是否合法");
        SkeletonEngine skeletonEngine = SKELETON_HANDLER_MAP.get(skeletonType);
        if (skeletonEngine == null) {
            return ResultUtils.fail("脚手架策略未被实现");
        } //不存在拦截： 策略未被实现
        skeletonEngine.build(skeleton);
        return ResultUtils.ok();
    }

}

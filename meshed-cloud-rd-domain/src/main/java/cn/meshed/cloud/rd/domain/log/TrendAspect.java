package cn.meshed.cloud.rd.domain.log;

import cn.meshed.cloud.aspect.ResolverKit;
import cn.meshed.cloud.rd.domain.project.constant.TrendLogLevelEnum;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
@Aspect
@Slf4j
public class TrendAspect {

    private final ResolverKit resolverKit;
    private final ApplicationContext applicationContext;

    @Pointcut("@annotation(cn.meshed.cloud.rd.domain.log.Trend)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object proceed = null;
        String key = null;
        String content = null;
        //获取运行时方法
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        try {
            if (method.isAnnotationPresent(Trend.class)) {
                Trend trend = method.getAnnotation(Trend.class);
                key = resolverKit.resolverContent(trend.key(), proceedingJoinPoint);
                content = resolverKit.resolverContent(trend.content(), proceedingJoinPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SysException sysException = null;

        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            sysException = new SysException(throwable.getMessage());
        }
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(content)) {
            //产生日志
            log(proceed, key, content, sysException);
        }


        //如果有异常抛出
        if (sysException != null) {
            throw sysException;
        }

        return proceed;
    }

    private void log(Object proceed, String key, String content, SysException sysException) {
        TrendLog trendLog = null;
        if (sysException != null) {
            trendLog = new TrendLog(key, TrendLogLevelEnum.EXCEPTION, content + "|" + sysException.getMessage());
        } else if (proceed instanceof Response) {
            Response response = (Response) proceed;
            if (response.isSuccess()) {
                trendLog = new TrendLog(key, TrendLogLevelEnum.INFO, content);
            } else {
                trendLog = new TrendLog(key, TrendLogLevelEnum.ERROR, content + "|" + response.getErrMessage());
            }
        } else {
            trendLog = new TrendLog(key, TrendLogLevelEnum.WARN, content + "|" + proceed.getClass().getName());

        }
        applicationContext.publishEvent(new TrendEvent(trendLog));
    }
}

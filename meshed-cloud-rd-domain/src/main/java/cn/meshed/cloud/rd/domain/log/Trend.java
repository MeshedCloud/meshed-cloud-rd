package cn.meshed.cloud.rd.domain.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trend {

    /**
     * 项目唯一标识
     *
     * @return 标识
     */
    String key();

    /**
     * 动态内容
     *
     * @return 内容
     */
    String content();

}

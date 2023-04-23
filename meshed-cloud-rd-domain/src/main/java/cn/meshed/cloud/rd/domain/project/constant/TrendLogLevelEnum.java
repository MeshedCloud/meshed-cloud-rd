package cn.meshed.cloud.rd.domain.project.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * <h1>动态日志等级</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@AllArgsConstructor
@Getter
@ToString
public enum TrendLogLevelEnum {

    /**
     * 信息
     */
    INFO(1, "INFO"),
    /**
     * 错误
     */
    ERROR(2, "ERROR"),
    /**
     * 警告
     */
    WARN(3, "WARN"),
    /**
     * 异常
     */
    EXCEPTION(3, "EXCEPTION"),
    ;

    @EnumValue
    private final int value;
    private final String key;

}

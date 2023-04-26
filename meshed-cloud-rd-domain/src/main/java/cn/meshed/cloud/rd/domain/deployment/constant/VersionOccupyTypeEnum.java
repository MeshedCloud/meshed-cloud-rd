package cn.meshed.cloud.rd.domain.deployment.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@AllArgsConstructor
@Getter
@ToString
public enum VersionOccupyTypeEnum {

    /**
     * 服务
     */
    SERVICE(0, "SERVICE"),
    /**
     * 服务
     */
    MODEL(1, "MODEL"),
    ;

    @EnumValue
    private final int value;
    private final String ext;
}

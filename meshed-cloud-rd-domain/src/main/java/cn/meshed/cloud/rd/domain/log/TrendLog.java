package cn.meshed.cloud.rd.domain.log;

import cn.meshed.cloud.rd.domain.project.constant.TrendLogLevelEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-04-22
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class TrendLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;

    /**
     * 项目动态所属项目
     */
    private String projectKey;

    /**
     * 项目动态等级
     */
    private TrendLogLevelEnum level;

    /**
     * 项目动态信息
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime time;

    public TrendLog(String projectKey, TrendLogLevelEnum level, String message) {
        this.projectKey = projectKey;
        this.level = level;
        this.message = message;
        this.time = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return projectKey + '|' + level.name() + "|" + message;
    }
}

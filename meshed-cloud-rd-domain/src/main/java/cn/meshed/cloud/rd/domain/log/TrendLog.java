package cn.meshed.cloud.rd.domain.log;

import cn.meshed.cloud.rd.log.enums.TrendLogLevelEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-04-22
 */
@NoArgsConstructor
@Data
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
    private Date time;

    public TrendLog(String projectKey, TrendLogLevelEnum level, String message) {
        this.projectKey = projectKey;
        this.level = level;
        if (StringUtils.isNotBlank(message)) {
            if (message.length() > 100) {
                this.message = message.substring(0, 100);
            } else {
                this.message = message;
            }
        }
        this.time = new Date();
    }

    @Override
    public String toString() {
        return projectKey + '|' + level.name() + "|" + message;
    }
}

package cn.meshed.cloud.rd.log.gatewayimpl.database.dataobject;

import cn.meshed.cloud.rd.log.enums.TrendLogLevelEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_trend_log")
public class TrendLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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


}

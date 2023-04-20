package cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-04-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_enum_value")
public class EnumValueDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 枚举值
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 模型ID
     */
    private String mId;

    /**
     * 枚举名称
     */
    private String name;

    /**
     * 枚举值
     */
    private Integer value;

    /**
     * 枚举扩展数据
     */
    private String ext;


}

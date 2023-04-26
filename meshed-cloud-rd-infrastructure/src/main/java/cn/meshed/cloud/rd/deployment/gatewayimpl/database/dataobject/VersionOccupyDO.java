package cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject;

import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 版本占用接口/模型
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_version_occupy")
public class VersionOccupyDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 版本ID
     */
    private Long versionId;

    /**
     * 类型：接口/模型
     */
    private ServiceModelTypeEnum type;

    /**
     * 关联编码
     */
    private String relationId;


}

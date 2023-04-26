package cn.meshed.cloud.rd.domain.deployment;

import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;
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
public class VersionOccupy implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;

    /**
     * 版本ID
     */
    private Long versionId;

    /**
     * 类型：接口/模型/项目
     */
    private ServiceModelTypeEnum type;

    /**
     * 关联编码
     */
    private String relationId;


}

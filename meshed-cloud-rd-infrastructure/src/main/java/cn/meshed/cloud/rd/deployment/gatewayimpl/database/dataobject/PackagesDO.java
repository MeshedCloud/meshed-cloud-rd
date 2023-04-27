package cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject;

import cn.meshed.cloud.rd.deployment.enums.PackagesTypeEnum;
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
 * @since 2023-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_packages")
public class PackagesDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 雪花ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 制品名称（中文）
     */
    private String name;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 制品ID
     */
    private String artifactId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 所属项目key
     */
    private String projectKey;

    /**
     * 制品类型
     */
    private PackagesTypeEnum type;


}

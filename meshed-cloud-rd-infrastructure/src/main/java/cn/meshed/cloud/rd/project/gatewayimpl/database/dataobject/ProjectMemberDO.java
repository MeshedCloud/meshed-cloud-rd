package cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject;

import cn.meshed.cloud.entity.BaseEntity;
import cn.meshed.cloud.rd.project.enums.ProjectRoleEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目成员
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_project_member")
public class ProjectMemberDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目key
     */
    private String projectKey;

    /**
     * 成员账号id
     */
    private Long uid;

    /**
     * 第三方账号(仓库)
     */
    private String thirdUid;

    /**
     * 项目成员的角色：管理员/开发者/浏览者
     */
    private ProjectRoleEnum projectRole;


}

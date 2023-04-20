package cn.meshed.cloud.rd.domain.repo;

import lombok.Data;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class AddRepositoryMember {

    /**
     * 仓库用户ID
     */
    private String repoUid;
    /**
     * 授权权限
     */
    private Integer accessLevel;
}

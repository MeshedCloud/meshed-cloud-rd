package cn.meshed.cloud.rd.domain.project;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * <h1>研发成员</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Member {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;

    /**
     * 账号id
     */
    private Long uid;

    /**
     * 第三方账号(仓库)
     */
    private String thirdUid;

}

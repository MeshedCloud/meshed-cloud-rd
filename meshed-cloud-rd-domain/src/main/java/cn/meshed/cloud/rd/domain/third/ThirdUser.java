package cn.meshed.cloud.rd.domain.third;

import lombok.Data;

/**
 * <h1>第三方用户信息</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class ThirdUser {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 账号名称
     */
    private String userName;
    /**
     * 初始化密码
     */
    private String initPassword;
    /**
     * 指定用户在登录时是否需要修改密码
     */
    private boolean passwordResetRequired;
    /**
     * 评论
     */
    private String comments;
    /**
     * 显示名称
     */
    private String displayName;
    /**
     * 手机号
     */
    private String mobilePhone;
    /**
     * 邮箱
     */
    private String email;

}

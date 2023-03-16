package cn.meshed.cloud.rd.domain.project;

import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.RequestTypeEnum;
import cn.meshed.cloud.rd.project.enums.ServiceAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceTypeEnum;
import lombok.Data;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Data
public class ServiceItem {

    /**
     * uuid
     */
    private String uuid;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务分组uri（URI前缀）
     */
    private String preUri;
    /**
     * 服务uri
     */
    private String uri;

    /**
     * 服务所属分组
     */
    private String groupId;

    /**
     * 服务所属分组名称
     */
    private String groupName;

    /**
     * 服务版本号
     */
    private Long version;

    /**
     * 服务负责人ID
     */
    private Long ownerId;

    /**
     * 服务授权码，用于注册身份安全
     */
    private String identifier;

    /**
     * 服务请求类型
     */
    private RequestTypeEnum requestType;

    /**
     * 服务请求类型
     */
    private ServiceTypeEnum type;

    /**
     * 服务级别 当前仅支持跟随项目性质（组件）
     */
    private ServiceAccessModeEnum accessMode;

    /**
     * 服务状态
     */
    private ServiceModelStatusEnum status;

    /**
     * 版本状态
     */
    private ReleaseStatusEnum releaseStatus;


}

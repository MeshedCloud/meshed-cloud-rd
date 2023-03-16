package cn.meshed.cloud.rd.project.convertor;


import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ServiceDO;
import cn.meshed.cloud.utils.CopyUtils;

import java.io.Serializable;

/**
 * <h1>模型转换</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public class ServiceConvertor implements Serializable {

    public static ServiceDO toEntity(Service service, ServiceDO serviceDO) {

        if (serviceDO == null) {
            return CopyUtils.copy(service, ServiceDO.class);
        }
        //如果是已经存在正式版本，升级版本
        if (ReleaseStatusEnum.RELEASE == serviceDO.getReleaseStatus()) {
            serviceDO.setVersion(serviceDO.getVersion() + 1);
            //清除UUID使其新建
            serviceDO.setUuid(null);
        }
        //即使已经是快照/发布中都会回归到编辑状态
        serviceDO.setReleaseStatus(ReleaseStatusEnum.EDIT);
        serviceDO.setName(serviceDO.getName());
        serviceDO.setDescription(serviceDO.getDescription());
        return serviceDO;
    }

}

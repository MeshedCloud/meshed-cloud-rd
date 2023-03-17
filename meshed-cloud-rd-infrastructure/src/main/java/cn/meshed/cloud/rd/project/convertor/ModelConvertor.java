package cn.meshed.cloud.rd.project.convertor;


import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ModelDO;
import cn.meshed.cloud.utils.CopyUtils;

import java.io.Serializable;

/**
 * <h1>模型转换</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public class ModelConvertor implements Serializable {

    public static ModelDO toEntity(Model model, ModelDO modelDO) {
        if (modelDO == null) {
            return CopyUtils.copy(model, ModelDO.class);
        }

        //如果是已经存在正式版本，升级版本
        if (ReleaseStatusEnum.RELEASE == model.getReleaseStatus()) {
            modelDO.setVersion(modelDO.getVersion() + 1);
            //清除UUID使其新建
            modelDO.setUuid(null);
        }

        //更新为业务指定的类型
        modelDO.setReleaseStatus(model.getReleaseStatus());
        modelDO.setName(model.getName());
        modelDO.setDescription(modelDO.getDescription());
        return modelDO;
    }

}

package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.project.command.ServiceModelPublishCmd;
import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceModelPublishCmdExe implements CommandExecute<ServiceModelPublishCmd, Response> {

    private final ServiceGateway serviceGateway;
    private final ModelGateway modelGateway;

    /**
     * @param serviceModelPublishCmd
     * @return
     */
    @Transactional
    @Override
    public Response execute(ServiceModelPublishCmd serviceModelPublishCmd) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(serviceModelPublishCmd.getUuids()), "编码不存在");
        AssertUtils.isTrue(serviceModelPublishCmd.getType() != null, "类型不能为空");
        AssertUtils.isTrue(serviceModelPublishCmd.getReleaseStatus() != null, "状态不能为空");
        if (serviceModelPublishCmd.getType() == ServiceModelTypeEnum.SERVICE) {
            serviceGateway.batchUpdateStatus(serviceModelPublishCmd.getUuids(), null,
                    serviceModelPublishCmd.getReleaseStatus());
        } else if (serviceModelPublishCmd.getType() == ServiceModelTypeEnum.MODEL) {
            modelGateway.batchUpdateStatus(serviceModelPublishCmd.getUuids(), null,
                    serviceModelPublishCmd.getReleaseStatus());
        }
        return ResultUtils.ok();
    }


}

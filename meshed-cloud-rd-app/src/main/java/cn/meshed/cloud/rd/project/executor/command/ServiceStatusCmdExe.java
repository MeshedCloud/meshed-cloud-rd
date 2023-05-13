package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.deployment.VersionOccupy;
import cn.meshed.cloud.rd.domain.deployment.VersionOccupyGateway;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.project.command.ServiceStatusCmd;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
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
public class ServiceStatusCmdExe implements CommandExecute<ServiceStatusCmd, Response> {

    private final ServiceGateway serviceGateway;
    private final VersionOccupyGateway versionOccupyGateway;

    /**
     * @param serviceStatusCmd
     * @return
     */
    @Transactional
    @Override
    public Response execute(ServiceStatusCmd serviceStatusCmd) {
        Service service = serviceGateway.query(serviceStatusCmd.getUuid());
        AssertUtils.isTrue(service != null, "服务不存在");
        assert service != null;
        VersionOccupy occupy = versionOccupyGateway.query(ServiceModelTypeEnum.SERVICE, service.getUuid());
        AssertUtils.isTrue(occupy == null, "模型正在处理中无法操作");
        //完成必须从编辑状态转换过来
        if (ReleaseStatusEnum.PROCESSING.equals(serviceStatusCmd.getReleaseStatus())) {
            AssertUtils.isTrue(service.getReleaseStatus() == ReleaseStatusEnum.EDIT, "服务当前并非编辑状态，无法修改为完成");
            AssertUtils.isTrue(serviceGateway.checkLegal(service.getUuid()), "服务参数类型存在编辑或删除情况");
        } else if (ReleaseStatusEnum.EDIT.equals(serviceStatusCmd.getReleaseStatus())) {
            AssertUtils.isTrue(service.getReleaseStatus() == ReleaseStatusEnum.PROCESSING, "服务当前并非进行状态，无法撤销");
        } else if (ReleaseStatusEnum.DISCARD.equals(serviceStatusCmd.getReleaseStatus())) {
            //快照和发行才可以废弃
            AssertUtils.isTrue(service.getReleaseStatus() == ReleaseStatusEnum.SNAPSHOT
                    || service.getReleaseStatus() == ReleaseStatusEnum.RELEASE, "服务当前并非快照/发行状态，无法撤销");
        }
        boolean op = serviceGateway.updateStatus(serviceStatusCmd.getUuid(),
                serviceStatusCmd.getStatus(), serviceStatusCmd.getReleaseStatus());
        return ResultUtils.of(op);
    }


}

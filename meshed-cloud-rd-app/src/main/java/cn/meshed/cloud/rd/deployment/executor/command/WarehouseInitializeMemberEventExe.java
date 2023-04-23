package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.EventExecute;
import cn.meshed.cloud.rd.deployment.event.WarehouseInitializeEvent;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.domain.project.gateway.MemberGateway;
import cn.meshed.cloud.rd.project.command.ProjectMemberCmd;
import cn.meshed.cloud.rd.project.enums.ProjectRoleEnum;
import cn.meshed.cloud.rd.project.executor.command.ProjectMemberCmdExe;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * <h1>仓库成员初始化</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class WarehouseInitializeMemberEventExe implements EventExecute<WarehouseInitializeEvent, Response> {

    private final ProjectMemberCmdExe projectMemberCmdExe;
    private final MemberGateway memberGateway;

    /**
     * <h1>执行器</h1>
     *
     * @param warehouseInitializeEvent 执行器 {@link WarehouseInitializeEvent}
     * @return {@link Void}
     */
    @Trend(key = "#{warehouseInitializeEvent.key}", content = "#{warehouseInitializeEvent.repositoryName}+仓库成员初始化")
    @Override
    public Response execute(WarehouseInitializeEvent warehouseInitializeEvent) {
        ProjectMemberCmd projectMemberCmd = new ProjectMemberCmd();
        projectMemberCmd.setProjectKey(warehouseInitializeEvent.getProjectKey());
        projectMemberCmd.setProjectRole(ProjectRoleEnum.ADMIN);
        Long currentUid = Long.valueOf(warehouseInitializeEvent.getOperator().getId());
        Integer mid = memberGateway.queryIdByUid(currentUid);
        if (mid == null) {
            return ResultUtils.fail("成员不存在，系统存在权限风险");
        }
        projectMemberCmd.setMemberIds(Collections.singletonList(mid));
        return projectMemberCmdExe.execute(projectMemberCmd);
    }
}

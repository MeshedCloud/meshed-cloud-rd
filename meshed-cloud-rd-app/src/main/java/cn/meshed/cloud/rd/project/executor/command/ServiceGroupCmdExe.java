package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.DomainGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.rd.project.command.ServiceGroupCmd;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.SingleResponse;
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
public class ServiceGroupCmdExe implements CommandExecute<ServiceGroupCmd, SingleResponse<ServiceGroup>> {

    private final ServiceGroupGateway serviceGroupGateway;
    private final ProjectGateway projectGateway;
    private final DomainGateway domainGateway;

    /**
     * <h1>执行器</h1>
     *
     * @param serviceGroupCmd 执行器 {@link ServiceGroupCmd}
     * @return {@link SingleResponse<ServiceGroup>}
     */
    @Trend(key = "#{serviceGroupCmd.projectKey}", content = "新增服务分组:+#{serviceGroupCmd.name}")
    @Transactional
    @Override
    public SingleResponse<ServiceGroup> execute(ServiceGroupCmd serviceGroupCmd) {
        AssertUtils.isTrue(serviceGroupCmd != null, "参数不能为空");
        ServiceGroup serviceGroup = CopyUtils.copy(serviceGroupCmd, ServiceGroup.class);
        assert serviceGroup != null;
        serviceGroup.setDomainKey(serviceGroupCmd.getDomain());
        String projectKey = serviceGroup.getProjectKey();
        Project project = projectGateway.queryByKey(projectKey);
        AssertUtils.isTrue(project != null, "归属项目并不存在");
        assert project != null;
        serviceGroup.initServiceGroup(serviceGroupCmd.getKey(), project.getBasePackage());
        //领域未添加时添加
        if (serviceGroupGateway.existGroupClassName(serviceGroup.getProjectKey(), serviceGroup.getClassName())) {
            return ResultUtils.fail("服务分组已经存在");
        }
        //领域未添加时添加
        if (!domainGateway.existDomainKey(serviceGroup.getDomainKey())) {
            return ResultUtils.fail("领域未新增");
        }

        ServiceGroup group = serviceGroupGateway.save(serviceGroup);
        return ResultUtils.of(group);
    }
}

package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.project.Field;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.Service;
import cn.meshed.cloud.rd.domain.project.ServiceGroup;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ServiceGroupGateway;
import cn.meshed.cloud.rd.project.command.ServiceCmd;
import cn.meshed.cloud.rd.project.enums.BaseGenericsEnum;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ServiceTypeEnum;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * <h1>服务存储处理器</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceCmdExe implements CommandExecute<ServiceCmd, Response> {

    private final ServiceGateway serviceGateway;
    private final ServiceGroupGateway serviceGroupGateway;
    private final ModelGateway modelGateway;

    private final ProjectGateway projectGateway;


    /**
     * @param serviceCmd
     * @return
     */
    @Transactional
    @Override
    public Response execute(ServiceCmd serviceCmd) {
        if (ServiceAccessModeEnum.AUTHORIZE == serviceCmd.getAccessMode()) {
            AssertUtils.isTrue(StringUtils.isNotBlank(serviceCmd.getIdentifier()), "授权模式下，授权码不能为空");
        }
        AssertUtils.isTrue(StringUtils.isNotBlank(serviceCmd.getGroupId()), "分组编码不能为空");
        //查询项目，判断项目是否存在，获取项目信息
        ServiceGroup serviceGroup = serviceGroupGateway.query(serviceCmd.getGroupId());
        AssertUtils.isTrue(serviceGroup != null, "服务分组不存在");
        Service service = CopyUtils.copy(serviceCmd, Service.class);
        assert serviceGroup != null;
        service.setGroupId(serviceGroup.getUuid());
        service.setProjectKey(serviceGroup.getProjectKey());
        service.setClassName(serviceGroup.getClassName());

        Project project = projectGateway.queryByKey(serviceGroup.getProjectKey());
        AssertUtils.isTrue(project != null, "服务分组归属系统异常不存在");
        service.setType(serviceGroup.getType());

        if (StringUtils.isBlank(service.getUuid())) {
            service.initService(serviceGroup);
        }
        //请求和响应字段处理
        Set<Field> requests = CopyUtils.copySetProperties(serviceCmd.getRequests(), Field::new);
        Set<Field> responses = CopyUtils.copySetProperties(serviceCmd.getResponses(), Field::new);

        //排除rpc无需的信息
        if (ServiceTypeEnum.RPC == serviceGroup.getType()) {
            handleRpc(requests);
        }
        assert project != null;

        Set<Model> newModels = service.handleFields(requests, responses);
        if (CollectionUtils.isNotEmpty(newModels)) {
            newModels.forEach(model -> {
                model.setProjectKey(serviceGroup.getProjectKey());
                model.setDomainKey(serviceGroup.getDomainKey());
                model.buildPackageName(project.getBasePackage());
                //内部操作的模型直接加入待发布队列
                model.setReleaseStatus(ReleaseStatusEnum.PROCESSING);
            });
            //批量新增产生的新模型
            AssertUtils.isTrue(modelGateway.batchSaveOrOnlyUpdateField(project.getKey(), newModels), "产生模型失败");
        }

        String uuid = serviceGateway.save(service);
        return ResultUtils.of(uuid);
    }

    private void handleRpc(Set<Field> fields) {
        fields.forEach(field -> {
            //RPC 无需路径参数和JSON特殊标识，避免异常出现
            if (field.getGeneric() == BaseGenericsEnum.JSON || field.getGeneric() == BaseGenericsEnum.PATH_VARIABLE) {
                field.setGeneric(BaseGenericsEnum.NONE);
            }
        });
    }
}

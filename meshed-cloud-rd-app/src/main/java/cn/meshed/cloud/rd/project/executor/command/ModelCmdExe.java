package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.project.Field;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.DomainGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectGateway;
import cn.meshed.cloud.rd.project.command.ModelCmd;
import cn.meshed.cloud.rd.project.data.RequestFieldDTO;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ModelCmdExe implements CommandExecute<ModelCmd, Response> {

    private final ModelGateway modelGateway;
    private final ProjectGateway projectGateway;
    private final DomainGateway domainGateway;

    /**
     * @param modelCmd
     * @return
     */
    @Transactional
    @Override
    public Response execute(ModelCmd modelCmd) {
        Model model = CopyUtils.copy(modelCmd, Model.class);
        model.setDomainKey(modelCmd.getDomain());
        AssertUtils.isTrue(StringUtils.isNotBlank(modelCmd.getProjectKey()), "项目Key不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(modelCmd.getDomain()), "所属领域不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(modelCmd.getKey()), "模型英文名称不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(modelCmd.getName()), "模型中文名称不能为空");

        if (StringUtils.isBlank(model.getUuid())) {
            //查询项目，判断项目是否存在，获取项目信息
            //仅在新增时处理此逻辑，项目确保不能被删除是首要前提
            String projectKey = model.getProjectKey();
            Project project = projectGateway.queryByKey(projectKey);
            AssertUtils.isTrue(project != null, "归属项目并不存在");
            //领域未添加时添加
            if (!domainGateway.existDomainKey(model.getDomainKey())) {
                return ResultUtils.fail("领域未新增");
            }
            assert project != null;
            model.initModel(project, modelCmd.getKey());
        }
        List<RequestFieldDTO> fields = modelCmd.getFields();
        if (CollectionUtils.isNotEmpty(fields)) {
            model.setFields(CopyUtils.copySetProperties(fields, Field::new));
        }

        String uuid = modelGateway.save(model);
        return ResultUtils.of(uuid);
    }
}

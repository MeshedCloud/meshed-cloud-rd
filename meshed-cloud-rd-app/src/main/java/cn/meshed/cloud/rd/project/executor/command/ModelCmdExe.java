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
import cn.meshed.cloud.rd.project.enums.OperateEnum;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.SysException;
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

        AssertUtils.isTrue(StringUtils.isNotBlank(modelCmd.getProjectKey()), "项目Key不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(modelCmd.getDomain()), "所属领域不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(modelCmd.getKey()), "模型英文名称不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(modelCmd.getName()), "模型中文名称不能为空");
        if (OperateEnum.COPY == modelCmd.getOperate()) {
            //拷贝业务,无论是否承担UUID，一律清除UUID，拷贝信息直接在前端完成
            //后续会进入新增逻辑，同名类是不允许的
            modelCmd.setUuid(null);
        }
        Model model = null;
        //初始化模型
        if (StringUtils.isBlank(modelCmd.getUuid())) {
            //初始化模型克隆前端的数据
            model = CopyUtils.copy(modelCmd, Model.class);
            model.setDomainKey(modelCmd.getDomain());
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
            AssertUtils.isTrue(!modelGateway.existClassName(model.getProjectKey(), model.getClassName()),
                    "模型在项目中已经存在");
        } else if (OperateEnum.EDIT == modelCmd.getOperate()) {
            //如果已经存在,去获取旧的，如果当前是正式版本就
            model = modelGateway.query(modelCmd.getUuid());

            //如果是已经存在正式版本，升级版本
            if (ReleaseStatusEnum.RELEASE == model.getReleaseStatus()) {
                model.setVersion(model.getVersion() + 1);
                //清除UUID使其新建
                model.setUuid(null);
            }

            //回归到编辑状态
            model.setReleaseStatus(ReleaseStatusEnum.EDIT);
        } else {
            throw new SysException("不存在此操作:" + modelCmd.getOperate());
        }
        List<RequestFieldDTO> fields = modelCmd.getFields();
        if (CollectionUtils.isNotEmpty(fields)) {
            model.setFields(CopyUtils.copySetProperties(fields, Field::new));
        }

        String uuid = modelGateway.save(model);
        return ResultUtils.of(uuid);
    }
}

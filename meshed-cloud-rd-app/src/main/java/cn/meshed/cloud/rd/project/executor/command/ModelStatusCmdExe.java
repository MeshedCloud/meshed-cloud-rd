package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.deployment.VersionOccupy;
import cn.meshed.cloud.rd.domain.deployment.VersionOccupyGateway;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.command.ModelStatusCmd;
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
public class ModelStatusCmdExe implements CommandExecute<ModelStatusCmd, Response> {

    private final ModelGateway modelGateway;
    private final VersionOccupyGateway versionOccupyGateway;

    /**
     * @param modelStatusCmd
     * @return
     */
    @Transactional
    @Override
    public Response execute(ModelStatusCmd modelStatusCmd) {
        Model model = modelGateway.query(modelStatusCmd.getUuid());
        AssertUtils.isTrue(model != null, "模型不存在");
        assert model != null;
        VersionOccupy occupy = versionOccupyGateway.query(ServiceModelTypeEnum.MODEL, model.getUuid());
        AssertUtils.isTrue(occupy == null, "模型正在处理中无法操作");
        //完成必须从编辑状态转换过来
        if (ReleaseStatusEnum.PROCESSING.equals(modelStatusCmd.getReleaseStatus())) {
            AssertUtils.isTrue(model.getReleaseStatus() == ReleaseStatusEnum.EDIT, "模型当前并非编辑状态，无法修改为完成");
            AssertUtils.isTrue(modelGateway.checkLegal(model.getUuid()), "服务参数类型存在编辑或删除情况");
        } else if (ReleaseStatusEnum.EDIT.equals(modelStatusCmd.getReleaseStatus())) {
            //修改为编辑状态必须为进行中做撤回存在
            AssertUtils.isTrue(model.getReleaseStatus() == ReleaseStatusEnum.PROCESSING, "模型当前并非进行状态，无法撤销");
        } else if (ReleaseStatusEnum.DISCARD.equals(modelStatusCmd.getReleaseStatus())) {
            //快照和发行才可以废弃
            AssertUtils.isTrue(model.getReleaseStatus() == ReleaseStatusEnum.SNAPSHOT
                    || model.getReleaseStatus() == ReleaseStatusEnum.RELEASE, "模型当前并非快照/发行状态，无法撤销");
        }
        boolean op = modelGateway.updateStatus(modelStatusCmd.getUuid(),
                modelStatusCmd.getStatus(), modelStatusCmd.getReleaseStatus());
        return ResultUtils.of(op, "修改失败");
    }


}

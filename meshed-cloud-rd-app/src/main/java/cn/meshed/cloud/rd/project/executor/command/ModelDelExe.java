package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
public class ModelDelExe implements CommandExecute<String, Response> {

    private final ModelGateway modelGateway;

    /**
     * @param uuid
     * @return
     */
    @Transactional
    @Override
    public Response execute(String uuid) {
        AssertUtils.isTrue(StringUtils.isNotBlank(uuid), "服务编码不能为空");
        Model model = modelGateway.query(uuid);
        AssertUtils.isTrue(model != null, "模型不存在");
        assert model != null;
        AssertUtils.isTrue(ReleaseStatusEnum.EDIT == model.getReleaseStatus(), "非编辑状态无法删除");
        return ResultUtils.of(modelGateway.delete(uuid), "刪除失敗");
    }

}

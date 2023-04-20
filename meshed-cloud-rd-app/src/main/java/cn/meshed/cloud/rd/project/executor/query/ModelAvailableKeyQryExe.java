package cn.meshed.cloud.rd.project.executor.query;

import cn.hutool.core.util.StrUtil;
import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.dto.ShowType;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.query.ModelAvailableKeyQry;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ModelAvailableKeyQryExe implements QueryExecute<ModelAvailableKeyQry, Response> {

    private final ModelGateway modelGateway;

    /**
     * @param modelAvailableKeyQry 模型key查询
     * @return
     */
    @Override
    public Response execute(ModelAvailableKeyQry modelAvailableKeyQry) {
        AssertUtils.isTrue(StringUtils.isNotBlank(modelAvailableKeyQry.getProjectKey()), "项目唯一标识不能为空");
        AssertUtils.isTrue(StringUtils.isNotBlank(modelAvailableKeyQry.getKey()), "模型标识不能为空");
        AssertUtils.isTrue(modelAvailableKeyQry.getType() != null, "标识不能为空");

        String className = StrUtil.upperFirst(modelAvailableKeyQry.getKey()) + modelAvailableKeyQry.getType().getExt();
        return ResultUtils.of(!modelGateway.existClassName(modelAvailableKeyQry.getProjectKey(), className),
                "模型标识已经存在", ShowType.SILENT);
    }
}

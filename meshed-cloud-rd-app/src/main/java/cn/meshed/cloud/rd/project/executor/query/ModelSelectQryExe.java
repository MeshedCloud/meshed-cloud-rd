package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ModelSelectQryExe implements QueryExecute<String, SingleResponse<Set<String>>> {

    private final ModelGateway modelGateway;

    /**
     * @param projectKey
     * @return
     */
    @Override
    public SingleResponse<Set<String>> execute(String projectKey) {
        AssertUtils.isTrue(StringUtils.isNotBlank(projectKey), "项目唯一标识不能为空");
        Set<String> select = modelGateway.select(projectKey);
        return ResultUtils.of(select);
    }
}

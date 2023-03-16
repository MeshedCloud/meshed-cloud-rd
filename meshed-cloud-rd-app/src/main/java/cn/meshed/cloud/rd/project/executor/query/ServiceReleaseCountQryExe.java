package cn.meshed.cloud.rd.project.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.project.data.ServiceReleaseCountDTO;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ServiceReleaseCountQryExe implements QueryExecute<String, SingleResponse<ServiceReleaseCountDTO>> {
    /**
     * @param projectKey
     * @return
     */
    @Override
    public SingleResponse<ServiceReleaseCountDTO> execute(String projectKey) {
        return ResultUtils.ok();
    }
}

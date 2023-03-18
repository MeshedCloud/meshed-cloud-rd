package cn.meshed.cloud.rd.deployment.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.deployment.data.VersionDTO;
import cn.meshed.cloud.rd.deployment.query.VersionPageQry;
import cn.meshed.cloud.rd.domain.deployment.Version;
import cn.meshed.cloud.rd.domain.deployment.gateway.VersionGateway;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.PageResponse;
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
public class VersionPageQryExe implements QueryExecute<VersionPageQry, PageResponse<VersionDTO>> {

    private final VersionGateway versionGateway;

    /**
     * @param versionPageQry
     * @return
     */
    @Override
    public PageResponse<VersionDTO> execute(VersionPageQry versionPageQry) {
        PageResponse<Version> pageResponse = versionGateway.searchPageList(versionPageQry);
        return ResultUtils.copyPage(pageResponse, VersionDTO::new);
    }
}

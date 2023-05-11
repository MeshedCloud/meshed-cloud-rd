package cn.meshed.cloud.rd.deployment.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.deployment.data.VersionDTO;
import cn.meshed.cloud.rd.deployment.query.VersionPageQry;
import cn.meshed.cloud.rd.domain.deployment.Version;
import cn.meshed.cloud.rd.domain.deployment.gateway.VersionGateway;
import cn.meshed.cloud.utils.CopyUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        if (pageResponse.isEmpty()) {
            return ResultUtils.copyPage(pageResponse, VersionDTO::new);
        }
        List<VersionDTO> list = pageResponse.getData().stream().map(this::toDTO).collect(Collectors.toList());
        return PageResponse.of(list, pageResponse.getTotalCount(),
                pageResponse.getPageSize(), pageResponse.getPageIndex());
    }

    private VersionDTO toDTO(Version version) {
        VersionDTO dto = CopyUtils.copy(version, VersionDTO.class);
        dto.setEnvironments(version.getEnvironmentEnums());
        return dto;
    }
}

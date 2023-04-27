package cn.meshed.cloud.rd.deployment.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.deployment.data.PackagesDTO;
import cn.meshed.cloud.rd.deployment.enums.PackagesTypeEnum;
import cn.meshed.cloud.rd.deployment.query.PackagesPageQry;
import cn.meshed.cloud.rd.domain.deployment.Packages;
import cn.meshed.cloud.rd.domain.deployment.gateway.PackagesGateway;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1>制品分页查询</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class PackagesPageQryExe implements QueryExecute<PackagesPageQry, PageResponse<PackagesDTO>> {

    private final PackagesGateway packagesGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param pageQry 执行器 {@link PackagesPageQry}
     * @return {@link PageResponse<PackagesDTO>}
     */
    @Override
    public PageResponse<PackagesDTO> execute(PackagesPageQry pageQry) {
        pageQry.setType(PackagesTypeEnum.MAVEN);
        PageResponse<Packages> pageResponse = packagesGateway.searchPageList(pageQry);
        return ResultUtils.copyPage(pageResponse, PackagesDTO::new);
    }
}

package cn.meshed.cloud.rd.log.executor.query;

import cn.meshed.cloud.cqrs.QueryExecute;
import cn.meshed.cloud.rd.domain.log.TrendLog;
import cn.meshed.cloud.rd.domain.log.gateway.TrendLogGateway;
import cn.meshed.cloud.rd.log.data.TrendDTO;
import cn.meshed.cloud.rd.log.query.TrendPageQry;
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
public class TrendPageQryExe implements QueryExecute<TrendPageQry, PageResponse<TrendDTO>> {

    private final TrendLogGateway trendLogGateway;

    /**
     * <h1>查询执行器</h1>
     *
     * @param pageQry 分页查询 {@link TrendPageQry}
     * @return {@link PageResponse<TrendDTO>}
     */
    @Override
    public PageResponse<TrendDTO> execute(TrendPageQry pageQry) {
        PageResponse<TrendLog> pageResponse = trendLogGateway.searchPageList(pageQry);
        return ResultUtils.copyPage(pageResponse, TrendDTO::new);
    }
}

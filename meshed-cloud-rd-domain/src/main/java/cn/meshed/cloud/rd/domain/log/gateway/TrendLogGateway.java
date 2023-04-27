package cn.meshed.cloud.rd.domain.log.gateway;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.domain.log.TrendLog;
import cn.meshed.cloud.rd.log.query.TrendPageQry;
import com.alibaba.cola.dto.PageResponse;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface TrendLogGateway extends ISave<TrendLog, Boolean>, IPageList<TrendPageQry, PageResponse<TrendLog>> {
}

package cn.meshed.cloud.rd.log.gatewayimpl;

import cn.meshed.cloud.rd.domain.log.TrendLog;
import cn.meshed.cloud.rd.domain.project.gateway.TrendLogGateway;
import cn.meshed.cloud.rd.log.gatewayimpl.database.dataobject.TrendLogDO;
import cn.meshed.cloud.rd.log.gatewayimpl.database.mapper.TrendLogMapper;
import cn.meshed.cloud.utils.CopyUtils;
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
public class TrendLogGatewayImpl implements TrendLogGateway {

    private final TrendLogMapper trendLogMapper;

    /**
     * @param trendLog
     * @return
     */
    @Override
    public Boolean save(TrendLog trendLog) {
        if (StringUtils.isBlank(trendLog.getProjectKey())) {
            return false;
        }
        TrendLogDO trendLogDO = CopyUtils.copy(trendLog, TrendLogDO.class);
        return trendLogMapper.insert(trendLogDO) > 0;
    }
}

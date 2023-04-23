package cn.meshed.cloud.rd.log.executor.listener;

import cn.meshed.cloud.rd.domain.log.TrendEvent;
import cn.meshed.cloud.rd.domain.project.gateway.TrendLogGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class TrendListener implements ApplicationListener<TrendEvent> {

    private final TrendLogGateway trendLogGateway;

    @Override
    public void onApplicationEvent(TrendEvent trendEvent) {
        log.info(trendEvent.toString());
        if (trendEvent.getTrendLog() != null) {
            trendLogGateway.save(trendEvent.getTrendLog());
        }
    }
}

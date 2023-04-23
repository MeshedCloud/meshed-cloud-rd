package cn.meshed.cloud.rd.domain.log;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@Getter
public class TrendEvent extends ApplicationEvent {

    private TrendLog trendLog;

    public TrendEvent(TrendLog trendLog) {
        super(trendLog);
        this.trendLog = trendLog;
    }

    @Override
    public String toString() {
        return trendLog.toString();
    }
}

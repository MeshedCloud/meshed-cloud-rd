package cn.meshed.cloud.rd.deployment.strategy;

import cn.meshed.cloud.rd.ProviderApplication;
import cn.meshed.cloud.rd.deployment.gatewayimpl.strategy.ClientPublishHandler;
import cn.meshed.cloud.rd.domain.deployment.strategy.AsyncPublishStrategy;
import cn.meshed.cloud.rd.domain.deployment.strategy.Publish;
import cn.meshed.cloud.rd.domain.deployment.strategy.PublishType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@SpringBootTest(classes = ProviderApplication.class)
@RunWith(SpringRunner.class)
public class AsyncPublishStrategyTest {

    @Autowired
    private AsyncPublishStrategy asyncPublishStrategy;
    @Autowired
    private ClientPublishHandler clientPublishHandler;

    @Test
    public void asyncPublish() {

        Publish publish = new Publish();
        publish.setProjectKey("PROPERTY");
        publish.setSourceId("3243305");
        publish.setMessage("生成客户端接口合模型");
        asyncPublishStrategy.asyncPublish(PublishType.CLIENT, publish);

    }
}
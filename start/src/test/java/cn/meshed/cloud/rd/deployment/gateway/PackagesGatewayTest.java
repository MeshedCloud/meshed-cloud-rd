package cn.meshed.cloud.rd.deployment.gateway;

import cn.meshed.cloud.rd.ProviderApplication;
import cn.meshed.cloud.rd.deployment.enums.PackagesTypeEnum;
import cn.meshed.cloud.rd.domain.deployment.Packages;
import cn.meshed.cloud.rd.domain.deployment.gateway.PackagesGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@SpringBootTest(classes = ProviderApplication.class)
@RunWith(SpringRunner.class)
public class PackagesGatewayTest {

    @Autowired
    private PackagesGateway packagesGateway;

    @Test
    @Transactional
    public void execute() {
        Packages packages = new Packages();
        packages.setType(PackagesTypeEnum.MAVEN);
        packages.setProjectKey("UNKNOWN");
        packages.setName("微服务父坐标1");
        packages.setGroupId("cn.meshed.cloud");
        packages.setArtifactId("meshed-base-parent");
        packages.setVersion("1.0.0");
        packagesGateway.save(packages);
    }

}

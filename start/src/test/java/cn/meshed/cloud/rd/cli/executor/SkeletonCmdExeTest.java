package cn.meshed.cloud.rd.cli.executor;

import cn.meshed.cloud.rd.ProviderApplication;
import cn.meshed.cloud.rd.cli.executor.command.SkeletonCmdExe;
import cn.meshed.cloud.rd.domain.cli.EngineTemplate;
import cn.meshed.cloud.rd.domain.cli.Skeleton;
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
public class SkeletonCmdExeTest {

    @Autowired
    private SkeletonCmdExe skeletonCmdExe;

    @Test
    public void execute() {
        Skeleton skeleton = new Skeleton();
        skeleton.setRepositoryName("meshed-cloud-property-client");
        skeleton.setBasePackage("cn.meshed.cloud.property");
        skeleton.setProjectKey("PROPERTY");
        EngineTemplate engineTemplate = new EngineTemplate();
        engineTemplate.setId("meshed-cloud-archetype");
        engineTemplate.setType(EngineTemplate.EngineTemplateType.MAVEN);
        engineTemplate.setOrigin("cn.meshed.archetype");
        engineTemplate.setTag("1.0.0-SNAPSHOT");
        skeleton.setEngineTemplate(engineTemplate);
        skeleton.setRepositoryId("3243305");
        skeletonCmdExe.execute(skeleton);
    }
}
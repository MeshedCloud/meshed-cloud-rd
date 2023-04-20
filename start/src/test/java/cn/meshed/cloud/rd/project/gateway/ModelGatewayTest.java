package cn.meshed.cloud.rd.project.gateway;

import cn.meshed.cloud.context.SecurityContext;
import cn.meshed.cloud.dto.Operator;
import cn.meshed.cloud.rd.ProviderApplication;
import cn.meshed.cloud.rd.domain.project.EnumValue;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.domain.project.gateway.ModelGateway;
import cn.meshed.cloud.rd.project.enums.ModelAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ModelTypeEnum;
import cn.meshed.cloud.rd.project.enums.ProjectAccessModeEnum;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelStatusEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@SpringBootTest(classes = ProviderApplication.class)
@RunWith(SpringRunner.class)
public class ModelGatewayTest {

    @Autowired
    private ModelGateway modelGateway;

    @Test
    public void searchPageList() {
    }

    @Test
    public void save() {
//        Model model = buildMockModel();
        Operator operator = new Operator("1");

        SecurityContext.setOperator(operator);
        Model model = buildMockEnumModel();

        modelGateway.save(model);
    }

    @NotNull
    private Model buildMockModel() {
        Model model = new Model();
        model.setAccessMode(ModelAccessModeEnum.PROTECTED);
        model.setDomainKey("project");
        model.setDescription("project xxx");
        model.setName("项目");
        model.setProjectKey("rd");
        model.setOwnerId(1L);
        model.setType(ModelTypeEnum.COMMAND);
        model.setStatus(ServiceModelStatusEnum.DEV);
        model.setReleaseStatus(ReleaseStatusEnum.EDIT);
        model.setSuperClass("DTO");
        Project project = new Project();
        project.setAccessMode(ProjectAccessModeEnum.NONE);
        project.setBasePackage("cn.meshed.cloud.rd");
        model.initModel(project, "Project");
        return model;
    }

    @NotNull
    private Model buildMockEnumModel() {
        Model model = new Model();
        model.setAccessMode(ModelAccessModeEnum.PROTECTED);
        model.setDomainKey("project");
        model.setDescription("project xxx");
        model.setName("项目");
        model.setProjectKey("rd");
        model.setOwnerId(1L);
        model.setType(ModelTypeEnum.ENUM);
        model.setStatus(ServiceModelStatusEnum.DEV);
        model.setReleaseStatus(ReleaseStatusEnum.EDIT);
        model.setSuperClass("DTO");
        Project project = new Project();
        project.setAccessMode(ProjectAccessModeEnum.NONE);
        project.setBasePackage("cn.meshed.cloud.rd");
        model.initModel(project, "Project");

        EnumValue enumValue = new EnumValue();
        enumValue.setValue(0);
        enumValue.setExt("test");
        enumValue.setName("TEST");
        model.setEnumValues(Collections.singleton(enumValue));
        return model;
    }

    @Test
    public void query() {

        Model model = modelGateway.query("7314f713aaf95bf0ba25bd38eadc516f");
        System.out.println(model);
    }
}
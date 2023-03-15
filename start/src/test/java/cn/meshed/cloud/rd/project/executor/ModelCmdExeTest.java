package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.ProviderApplication;
import cn.meshed.cloud.rd.project.command.ModelCmd;
import cn.meshed.cloud.rd.project.data.RequestFieldDTO;
import cn.meshed.cloud.rd.project.enums.ModelTypeEnum;
import cn.meshed.cloud.rd.project.executor.command.ModelCmdExe;
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
public class ModelCmdExeTest {

    @Autowired
    private ModelCmdExe modelCmdCmdExe;

    @Test
    public void execute() {
        modelCmdCmdExe.execute(buildMockModelCmd());
    }

    @NotNull
    private ModelCmd buildMockModelCmd() {
        ModelCmd modelCmd = new ModelCmd();
        modelCmd.setDomain("property");
        modelCmd.setDescription("property xxx");
        modelCmd.setKey("Property");
        modelCmd.setName("资产操作");
        modelCmd.setProjectKey("PROPERTY");
        modelCmd.setType(ModelTypeEnum.COMMAND);
        modelCmd.setSuperClass("DTO");
        RequestFieldDTO requestFieldDTO = new RequestFieldDTO();
        requestFieldDTO.setFieldType("String");
        requestFieldDTO.setFieldName("keyword");
        requestFieldDTO.setExplain("关键字");
        modelCmd.setFields(Collections.singletonList(requestFieldDTO));
        return modelCmd;
    }
}
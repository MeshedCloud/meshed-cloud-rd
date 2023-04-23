package cn.meshed.cloud.rd.deployment.gateway;

import cn.meshed.cloud.rd.ProviderApplication;
import cn.meshed.cloud.rd.deployment.command.WarehouseAddCmd;
import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.executor.command.WarehouseAddCmdExe;
import cn.meshed.cloud.rd.domain.deployment.Warehouse;
import com.alibaba.cola.dto.SingleResponse;
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
public class WarehouseAddCmdExeTest {

    @Autowired
    private WarehouseAddCmdExe warehouseAddCmdExe;

    @Test
    public void execute() {
        WarehouseAddCmd warehouseAddCmd = new WarehouseAddCmd();
        warehouseAddCmd.setProjectKey("PROPERTY");
        warehouseAddCmd.setRepoName("meshed-cloud-property");
        warehouseAddCmd.setDescription("xxxxx");
        warehouseAddCmd.setPurposeType(WarehousePurposeTypeEnum.SERVICE);
        warehouseAddCmd.setName("xxx");
        SingleResponse<Warehouse> execute = warehouseAddCmdExe.execute(warehouseAddCmd);
        System.out.println(execute);
    }
}
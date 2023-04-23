package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.EventExecute;
import cn.meshed.cloud.rd.deployment.command.WarehouseAddCmd;
import cn.meshed.cloud.rd.deployment.enums.WarehouseOperateEnum;
import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.executor.query.ScaffoldTemplateQryExe;
import cn.meshed.cloud.rd.domain.deployment.ScaffoldTemplate;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.rd.project.event.ProjectInitializeEvent;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <h1>仓库初始化项目</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class WarehouseInitByProjectEventExe implements EventExecute<ProjectInitializeEvent, Response> {

    private final ScaffoldTemplateQryExe scaffoldTemplateQryExe;
    private final WarehouseAddCmdExe warehouseAddCmdExe;

    /**
     * <h1>初始化仓库（代码构建会通过仓库创建完成事件构建）</h1>
     *
     * @param projectInitializeEvent 执行器 {@link ProjectInitializeEvent}
     * @return {@link Consumer<ProjectInitializeEvent>}
     */
    @Trend(key = "#{projectInitializeEvent.key}", content = "#{projectInitializeEvent.name}+项目仓库初始化")
    @Override
    public Response execute(ProjectInitializeEvent projectInitializeEvent) {
        if (CollectionUtils.isNotEmpty(projectInitializeEvent.getCodeTemplates())) {
            System.out.println("projectInitializeEvent");
            projectInitializeEvent.getCodeTemplates().stream().filter(Objects::nonNull)
                    //查询模板
                    .map(scaffoldTemplateQryExe::execute)
                    //判断非空
                    .filter(CollectionUtils::isNotEmpty)
                    //合流
                    .flatMap(Collection::stream)
                    //创建仓库
                    .forEach(template -> warehouseAddCmdExe.execute(buildWarehouseAddCmd(projectInitializeEvent, template)));
        }
        return ResultUtils.ok();
    }

    /**
     * 构建创建仓库参数
     *
     * @param projectInitializeEvent
     * @param template
     * @return
     */
    @NotNull
    private WarehouseAddCmd buildWarehouseAddCmd(ProjectInitializeEvent projectInitializeEvent, ScaffoldTemplate template) {
        WarehouseAddCmd warehouseAddCmd = new WarehouseAddCmd();
        warehouseAddCmd.setPurposeType(WarehousePurposeTypeEnum.valueOf(template.getType().toUpperCase()));
        warehouseAddCmd.setRepoName(getRepositoryName(template.getFormat(), projectInitializeEvent.getKey()));
        warehouseAddCmd.setProjectKey(projectInitializeEvent.getKey());
        warehouseAddCmd.setName(projectInitializeEvent.getName() + template.getTypeName());
        warehouseAddCmd.setDescription(projectInitializeEvent.getDescription());
        warehouseAddCmd.setEngineTemplate(template.getEngine());
        warehouseAddCmd.setOperate(WarehouseOperateEnum.NEW);
        return warehouseAddCmd;
    }

    /**
     * 格式化生成仓库名字
     *
     * @param format 格式
     * @param key    key
     * @return 仓库名字
     */
    private String getRepositoryName(String format, String key) {
        return String.format(format, key).toLowerCase();
    }

}

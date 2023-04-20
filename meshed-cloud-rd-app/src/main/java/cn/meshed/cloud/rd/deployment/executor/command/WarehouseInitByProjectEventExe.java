package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.EventExecute;
import cn.meshed.cloud.rd.deployment.command.WarehouseAddCmd;
import cn.meshed.cloud.rd.deployment.enums.WarehousePurposeTypeEnum;
import cn.meshed.cloud.rd.deployment.executor.query.ScaffoldTemplateQryExe;
import cn.meshed.cloud.rd.domain.deployment.ScaffoldTemplate;
import cn.meshed.cloud.rd.project.event.ProjectInitializeEvent;
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
public class WarehouseInitByProjectEventExe implements EventExecute<ProjectInitializeEvent, Void> {

    private final ScaffoldTemplateQryExe scaffoldTemplateQryExe;
    private final WarehouseAddCmdExe warehouseAddCmdExe;

    /**
     * <h1>初始化仓库（代码构建会通过仓库创建完成事件构建）</h1>
     *
     * @param projectInitializeEvent 执行器 {@link ProjectInitializeEvent}
     * @return {@link Consumer<ProjectInitializeEvent>}
     */
    @Override
    public Void execute(ProjectInitializeEvent projectInitializeEvent) {
        if (CollectionUtils.isNotEmpty(projectInitializeEvent.getCodeTemplates())) {
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
        return null;
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
        return String.format(format, key);
    }

}

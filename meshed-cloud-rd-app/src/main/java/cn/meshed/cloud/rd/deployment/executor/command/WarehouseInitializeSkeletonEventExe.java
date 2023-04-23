package cn.meshed.cloud.rd.deployment.executor.command;

import cn.meshed.cloud.cqrs.EventExecute;
import cn.meshed.cloud.rd.cli.executor.command.SkeletonCmdExe;
import cn.meshed.cloud.rd.cli.executor.query.EngineTemplateQryExe;
import cn.meshed.cloud.rd.deployment.event.WarehouseInitializeEvent;
import cn.meshed.cloud.rd.domain.cli.EngineTemplate;
import cn.meshed.cloud.rd.domain.cli.Skeleton;
import cn.meshed.cloud.rd.domain.log.Trend;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * <h1>脚手架构建项目</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class WarehouseInitializeSkeletonEventExe implements EventExecute<WarehouseInitializeEvent, Response> {

    private final SkeletonCmdExe skeletonCmdExe;
    private final EngineTemplateQryExe engineTemplateQryExe;


    /**
     * <h1>执行器</h1>
     *
     * @param warehouseInitializeEvent 执行器 {@link WarehouseInitializeEvent}
     * @return {@link Void}
     */
    @Trend(key = "#{warehouseInitializeEvent.projectKey}", content = "#{warehouseInitializeEvent.repositoryName}+初始化")
    @Override
    public Response execute(WarehouseInitializeEvent warehouseInitializeEvent) {
        System.out.println("skeleton test");

        //参数校验
        if (StringUtils.isBlank(warehouseInitializeEvent.getProjectKey())) {
            return ResultUtils.fail("项目key不能为空");
        }
        if (StringUtils.isBlank(warehouseInitializeEvent.getEngineTemplate())) {
            return ResultUtils.fail("引擎模板不不能为空");
        }
        EngineTemplate engineTemplate = engineTemplateQryExe.execute(warehouseInitializeEvent.getEngineTemplate());
        if (engineTemplate == null) {
            return ResultUtils.fail("引擎模板不不能为空");
        }
        Skeleton skeleton = buildSkeleton(warehouseInitializeEvent);
        skeleton.setEngineTemplate(engineTemplate);
        try {
            //构建脚手架
            return skeletonCmdExe.execute(skeleton);
        } catch (SysException sysException) {
            return ResultUtils.fail(sysException.getMessage());
        }

    }

    @NotNull
    private Skeleton buildSkeleton(WarehouseInitializeEvent warehouseInitializeEvent) {
        Skeleton skeleton = new Skeleton();
        skeleton.setBasePackage(warehouseInitializeEvent.getBasePackage());
        skeleton.setProjectKey(warehouseInitializeEvent.getProjectKey());
        skeleton.setRepositoryId(warehouseInitializeEvent.getRepositoryId());
        skeleton.setRepositoryName(warehouseInitializeEvent.getRepositoryName());
        return skeleton;
    }
}
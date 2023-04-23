package cn.meshed.cloud.rd.cli.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.cli.EngineTemplate;
import cn.meshed.cloud.rd.domain.cli.Skeleton;
import cn.meshed.cloud.rd.domain.cli.gateway.CliGateway;
import cn.meshed.cloud.rd.domain.cli.strategy.AsyncSkeletonStrategy;
import cn.meshed.cloud.rd.domain.cli.strategy.SkeletonType;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.SysException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class SkeletonCmdExe implements CommandExecute<Skeleton, Response> {

    private final CliGateway cliGateway;
    private final AsyncSkeletonStrategy skeletonStrategy;


    /**
     * <h1>执行器</h1>
     *
     * @param skeleton 执行器 {@link Skeleton}
     * @return {@link Response}
     */
    @Override
    public Response execute(Skeleton skeleton) {
        //校验
        skeleton.verification();
        skeletonStrategy.asyncBuild(getType(skeleton), skeleton);
        return ResultUtils.ok();
    }

    private SkeletonType getType(Skeleton skeleton) {
        EngineTemplate.EngineTemplateType type = skeleton.getEngineTemplate().getType();
        if (type == EngineTemplate.EngineTemplateType.MAVEN) {
            return SkeletonType.MAVEN;
        } else if (type == EngineTemplate.EngineTemplateType.GIT_TEMPLATE) {
            return SkeletonType.GIT_TEMPLATE;
        }
        throw new SysException("暂不支持：" + type);
    }
}

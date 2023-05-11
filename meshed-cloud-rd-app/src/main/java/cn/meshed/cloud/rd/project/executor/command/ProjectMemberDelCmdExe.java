package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.project.gateway.ProjectMemberGateway;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <h1>项目成员删除</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class ProjectMemberDelCmdExe implements CommandExecute<Integer, Response> {

    private final ProjectMemberGateway projectMemberGateway;

    /**
     * <h1>执行器</h1>
     *
     * @param id 项目成员ID
     * @return {@link Response}
     */
    @Override
    public Response execute(Integer id) {
        return ResultUtils.of(projectMemberGateway.delete(id), "修改失败");
    }
}

package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.project.command.ProjectChangeCmd;
import com.alibaba.cola.dto.Response;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public class ProjectChangeCmdExe implements CommandExecute<ProjectChangeCmd, Response> {
    /**
     * <h1>执行器</h1>
     *
     * @param cmd 执行器 {@link C}
     * @return {@link R}
     */
    @Override
    public Response execute(ProjectChangeCmd cmd) {
        return null;
    }
}

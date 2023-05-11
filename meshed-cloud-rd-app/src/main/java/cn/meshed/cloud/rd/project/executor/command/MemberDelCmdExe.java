package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.rd.domain.project.gateway.MemberGateway;
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
public class MemberDelCmdExe implements CommandExecute<Integer, Response> {
    private final MemberGateway memberGateway;

    /**
     * <h1>执行器</h1>
     *
     * @param id 成员ID
     * @return {@link Response}
     */
    @Override
    public Response execute(Integer id) {
        return ResultUtils.of(memberGateway.delete(id), "删除失败");
    }
}

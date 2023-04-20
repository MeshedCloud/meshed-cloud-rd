package cn.meshed.cloud.rd.project.executor.command;

import cn.meshed.cloud.cqrs.CommandExecute;
import cn.meshed.cloud.iam.account.data.UserDTO;
import cn.meshed.cloud.rd.domain.project.Member;
import cn.meshed.cloud.rd.domain.project.gateway.MemberGateway;
import cn.meshed.cloud.rd.domain.third.ThirdUser;
import cn.meshed.cloud.rd.domain.third.gateway.ThirdUserGateway;
import cn.meshed.cloud.rd.project.command.MemberCmd;
import cn.meshed.cloud.rd.wrapper.user.UserWrapper;
import cn.meshed.cloud.utils.AssertUtils;
import cn.meshed.cloud.utils.ResultUtils;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * <h1>项目成员删除</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@Component
public class MemberCmdExe implements CommandExecute<MemberCmd, Response> {

    private final MemberGateway memberGateway;
    private final ThirdUserGateway thirdUserGateway;
    private final UserWrapper userWrapper;

    /**
     * <h1>执行器</h1>
     *
     * @param memberCmd 项目成员ID
     * @return {@link Response}
     */
    @Override
    public Response execute(MemberCmd memberCmd) {
        AssertUtils.isTrue(CollectionUtils.isNotEmpty(memberCmd.getUserIds()), "成员参数不能为空");
        for (Long uId : memberCmd.getUserIds()) {
            newMember(uId);
        }
        return ResultUtils.ok();
    }

    private void newMember(Long uid) {
        UserDTO user = userWrapper.getUser(uid);
        ThirdUser thirdUser = thirdUserGateway.save(buildThirdUser(user));
        AssertUtils.isTrue(thirdUser != null, "导入用户失败");
        Member member = new Member();
        member.setUid(uid);
        assert thirdUser != null;
        member.setThirdUid(thirdUser.getUserId());
        memberGateway.save(member);
    }

    @NotNull
    private ThirdUser buildThirdUser(UserDTO user) {
        ThirdUser thirdUser = new ThirdUser();
        thirdUser.setUserName(user.getLoginId());
        thirdUser.setMobilePhone(user.getPhone());
        thirdUser.setEmail(user.getEmail());
        thirdUser.setDisplayName(user.getName());
        thirdUser.setPasswordResetRequired(true);
        thirdUser.setInitPassword("meshed123456");
        thirdUser.setComments("研发中心创建");
        return thirdUser;
    }
}

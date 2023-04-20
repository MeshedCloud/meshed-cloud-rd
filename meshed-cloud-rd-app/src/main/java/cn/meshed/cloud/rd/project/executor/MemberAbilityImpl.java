package cn.meshed.cloud.rd.project.executor;

import cn.meshed.cloud.rd.domain.project.ability.MemberAbility;
import cn.meshed.cloud.rd.project.command.MemberCmd;
import cn.meshed.cloud.rd.project.data.MemberDTO;
import cn.meshed.cloud.rd.project.executor.command.MemberCmdExe;
import cn.meshed.cloud.rd.project.executor.command.MemberDelCmdExe;
import cn.meshed.cloud.rd.project.executor.query.ImportMemberPageQryExe;
import cn.meshed.cloud.rd.project.executor.query.MemberPageQryExe;
import cn.meshed.cloud.rd.project.query.ImportMemberPageQry;
import cn.meshed.cloud.rd.project.query.MemberPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
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
public class MemberAbilityImpl implements MemberAbility {
    private final MemberPageQryExe memberPageQryExe;
    private final ImportMemberPageQryExe importMemberPageQryExe;
    private final MemberCmdExe memberCmdExe;
    private final MemberDelCmdExe memberDelCmdExe;

    /**
     * <h1>删除对象</h1>
     *
     * @param id 成员ID
     * @return {@link Response}
     */
    @Override
    public Response delete(Integer id) {
        return memberDelCmdExe.execute(id);
    }

    /**
     * <h1>分页搜索</h1>
     *
     * @param pageQry 分页参数
     * @return {@link PageResponse<MemberDTO>}
     */
    @Override
    public PageResponse<MemberDTO> searchPageList(MemberPageQry pageQry) {
        return memberPageQryExe.execute(pageQry);
    }

    /**
     * <h1>保存对象</h1>
     *
     * @param memberCmd 成员操作
     * @return {@link Response}
     */
    @Override
    public Response save(MemberCmd memberCmd) {
        return memberCmdExe.execute(memberCmd);
    }

    /**
     * 导入成员分页查询
     *
     * @param pageQry 分页参数
     * @return {@link PageResponse<MemberDTO>}
     */
    @Override
    public PageResponse<MemberDTO> searchImportPage(ImportMemberPageQry pageQry) {
        return importMemberPageQryExe.execute(pageQry);
    }
}

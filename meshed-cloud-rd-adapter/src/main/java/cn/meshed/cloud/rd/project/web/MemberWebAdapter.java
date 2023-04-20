package cn.meshed.cloud.rd.project.web;

import cn.meshed.cloud.rd.domain.project.ability.MemberAbility;
import cn.meshed.cloud.rd.project.MemberAdapter;
import cn.meshed.cloud.rd.project.command.MemberCmd;
import cn.meshed.cloud.rd.project.data.MemberDTO;
import cn.meshed.cloud.rd.project.query.ImportMemberPageQry;
import cn.meshed.cloud.rd.project.query.MemberPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
public class MemberWebAdapter implements MemberAdapter {

    private final MemberAbility memberAbility;

    /**
     * 列表
     *
     * @param memberPageQry 成员分页查询
     * @return {@link PageResponse<MemberDTO>}
     */
    @Override
    public PageResponse<MemberDTO> list(@Valid MemberPageQry memberPageQry) {
        return memberAbility.searchPageList(memberPageQry);
    }

    /**
     * 导入成员列表
     *
     * @param importMemberPageQry 成员导入分页查询
     * @return {@link PageResponse<MemberDTO>}
     */
    @Override
    public PageResponse<MemberDTO> importMemberList(@Valid ImportMemberPageQry importMemberPageQry) {
        return memberAbility.searchImportPage(importMemberPageQry);
    }

    /**
     * 新增成员
     *
     * @param memberCmd 成员信息
     * @return {@link Response}
     */
    @Override
    public Response add(@Valid MemberCmd memberCmd) {
        return memberAbility.save(memberCmd);
    }

    /**
     * 删除成员
     *
     * @param id 成员ID
     * @return {@link Response}
     */
    @Override
    public Response delete(Integer id) {
        return memberAbility.delete(id);
    }
}

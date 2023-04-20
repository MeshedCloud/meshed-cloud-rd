package cn.meshed.cloud.rd.domain.project.ability;

import cn.meshed.cloud.core.IDelete;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.project.command.MemberCmd;
import cn.meshed.cloud.rd.project.data.MemberDTO;
import cn.meshed.cloud.rd.project.query.ImportMemberPageQry;
import cn.meshed.cloud.rd.project.query.MemberPageQry;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface MemberAbility extends IPageList<MemberPageQry, PageResponse<MemberDTO>>,
        ISave<MemberCmd, Response>, IDelete<Integer, Response> {

    /**
     * 导入成员分页查询
     *
     * @param pageQry 分页参数
     * @return {@link PageResponse<MemberDTO>}
     */
    PageResponse<MemberDTO> searchImportPage(ImportMemberPageQry pageQry);
}

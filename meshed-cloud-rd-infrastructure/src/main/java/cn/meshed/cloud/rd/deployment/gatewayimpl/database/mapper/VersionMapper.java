package cn.meshed.cloud.rd.deployment.gatewayimpl.database.mapper;

import cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject.VersionDO;
import cn.meshed.cloud.rd.deployment.gatewayimpl.database.vo.VersionVO;
import cn.meshed.cloud.rd.deployment.query.VersionPageQry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-03-17
 */
public interface VersionMapper extends BaseMapper<VersionDO> {

    List<VersionVO> list(VersionPageQry versionPageQry);
}

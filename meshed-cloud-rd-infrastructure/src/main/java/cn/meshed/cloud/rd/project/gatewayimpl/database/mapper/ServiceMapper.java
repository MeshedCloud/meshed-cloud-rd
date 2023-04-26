package cn.meshed.cloud.rd.project.gatewayimpl.database.mapper;

import cn.meshed.cloud.mysql.EasyMapper;
import cn.meshed.cloud.rd.project.gatewayimpl.database.dataobject.ServiceDO;
import cn.meshed.cloud.rd.project.gatewayimpl.database.vo.ServiceVO;
import cn.meshed.cloud.rd.project.query.ServicePageQry;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-02-27
 */
public interface ServiceMapper extends EasyMapper<ServiceDO> {

    /**
     * 查询分页
     *
     * @param servicePageQry
     * @return
     */
    List<ServiceVO> list(ServicePageQry servicePageQry);

}

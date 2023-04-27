package cn.meshed.cloud.rd.deployment.gatewayimpl.database.dao.impl;

import cn.meshed.cloud.rd.deployment.gatewayimpl.database.dao.PackagesDao;
import cn.meshed.cloud.rd.deployment.gatewayimpl.database.dataobject.PackagesDO;
import cn.meshed.cloud.rd.deployment.gatewayimpl.database.mapper.PackagesMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author by Vincent Vic
 * @since 2023-04-27
 */
@Service
public class PackagesDaoImpl extends ServiceImpl<PackagesMapper, PackagesDO> implements PackagesDao {

}

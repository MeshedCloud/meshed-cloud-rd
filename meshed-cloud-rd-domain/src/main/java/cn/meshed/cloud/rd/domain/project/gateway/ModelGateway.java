package cn.meshed.cloud.rd.domain.project.gateway;

import cn.meshed.cloud.core.IBatchSave;
import cn.meshed.cloud.core.IDelete;
import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.IQuery;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.core.ISelect;
import cn.meshed.cloud.rd.domain.project.Model;
import cn.meshed.cloud.rd.project.enums.ReleaseStatusEnum;
import cn.meshed.cloud.rd.project.enums.ServiceModelStatusEnum;
import cn.meshed.cloud.rd.project.query.ModelPageQry;
import com.alibaba.cola.dto.PageResponse;

import java.util.Set;

/**
 * <h1>模型网关</h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ModelGateway extends ISave<Model, String>, IQuery<String, Model>,
        IBatchSave<Set<Model>, Integer>, ISelect<String, Set<String>>,
        IPageList<ModelPageQry, PageResponse<Model>>, IDelete<String, Boolean> {

    /**
     * 判断className是否在模型中是否已经存在
     *
     * @param projectKey 项目key 当项目key不存在时会判断这个系统的类名唯一值
     * @param className  类名
     * @return
     */
    boolean existClassName(String projectKey, String className);

    /**
     * 查询项目的待发布模型详情列表 (不含枚举模型)
     *
     * @param projectKey 项目key
     * @return 详情列表
     */
    Set<Model> waitPublishModelListByProject(String projectKey);

    /**
     * 查询项目的待发布枚举详情列表
     *
     * @param projectKey 项目key
     * @return 详情列表
     */
    Set<Model> waitPublishEnumListByProject(String projectKey);

    /**
     * 根据类名列表转换出包名列表
     *
     * @param classNames 类名
     * @return 返回
     */
    Set<String> scanPackageNameByClassNames(Set<String> classNames);

    /**
     * 根据类名列表转换出模型UUID列表
     *
     * @param classNames 类名
     * @return 返回
     */
    Set<String> selectUuidListByClassNames(Set<String> classNames);

    /**
     * 批量保存模型（含字段）或仅更新字段
     *
     * @param projectKey 项目唯一标识
     * @param models     模型
     * @return 成功与否
     */
    boolean batchSaveOrUpdate(String projectKey, Set<Model> models);

    /**
     * 更新状态
     *
     * @param uuid          编码
     * @param status        状态
     * @param releaseStatus 发行状态
     * @return 成功与否
     */
    boolean updateStatus(String uuid, ServiceModelStatusEnum status, ReleaseStatusEnum releaseStatus);

    /**
     * 批量更新状态
     *
     * @param uuids         编码列表
     * @param status        状态
     * @param releaseStatus 发行状态
     * @return 成功与否
     */
    boolean batchUpdateStatus(Set<String> uuids, ServiceModelStatusEnum status, ReleaseStatusEnum releaseStatus);

    /**
     * 模型检查合法性
     *
     * @param uuid
     * @return 合法性
     */
    boolean checkLegal(String uuid);

    /**
     * 通过类名检查模型合法性
     *
     * @param classNames 类名列表
     * @return 合法性
     */
    boolean checkLegalByClassNames(Set<String> classNames);
}

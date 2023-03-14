package cn.meshed.cloud.rd.domain.project.gateway;

import cn.meshed.cloud.core.IPageList;
import cn.meshed.cloud.core.ISave;
import cn.meshed.cloud.rd.domain.project.Project;
import cn.meshed.cloud.rd.project.query.ProjectPageQry;
import com.alibaba.cola.dto.PageResponse;

/**
 * <h1></h1>
 *
 * @author Vincent Vic
 * @version 1.0
 */
public interface ProjectGateway extends ISave<Project, Project>, IPageList<ProjectPageQry, PageResponse<Project>> {

    /**
     * 通过key查询
     *
     * @param key key
     * @return {@link Project}
     */
    Project queryByKey(String key);

    /**
     * 判断项目Key是否已经存在
     *
     * @param key 项目key
     * @return
     */
    boolean existKey(String key);
}

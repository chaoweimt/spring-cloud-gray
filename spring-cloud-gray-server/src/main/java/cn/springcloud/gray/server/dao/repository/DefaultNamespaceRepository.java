package cn.springcloud.gray.server.dao.repository;

import cn.springcloud.gray.server.dao.model.DefaultNamespaceDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author saleson
 * @date 2020-03-21 22:07
 */
@Repository
public interface DefaultNamespaceRepository extends JpaRepository<DefaultNamespaceDO, String>, JpaSpecificationExecutor<DefaultNamespaceDO> {

}

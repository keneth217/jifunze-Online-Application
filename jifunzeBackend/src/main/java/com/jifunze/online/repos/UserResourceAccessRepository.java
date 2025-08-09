package com.jifunze.online.repos;

import com.jifunze.online.entity.UserResourceAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserResourceAccessRepository extends JpaRepository<UserResourceAccess, Long> {
    
    List<UserResourceAccess> findByUserId(Long userId);
    
    List<UserResourceAccess> findByResourceId(Long resourceId);
    
    @Query("SELECT ura FROM UserResourceAccess ura WHERE ura.user.id = :userId AND ura.resource.id = :resourceId")
    Optional<UserResourceAccess> findByUserAndResource(@Param("userId") Long userId, @Param("resourceId") Long resourceId);
    
    @Query("SELECT COUNT(ura) FROM UserResourceAccess ura WHERE ura.user.id = :userId AND ura.resource.id = :resourceId")
    Long countByUserAndResource(@Param("userId") Long userId, @Param("resourceId") Long resourceId);
    
    Optional<UserResourceAccess> findByUserIdAndResourceId(Long userId, Long resourceId);
    
    boolean existsByUserIdAndResourceId(Long userId, Long resourceId);
}

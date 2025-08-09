package com.jifunze.online.repos;

import com.jifunze.online.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    
    Page<Resource> findByIsActiveTrue(Pageable pageable);
    
    Page<Resource> findByIsActiveTrueAndIsPremiumFalse(Pageable pageable);
    
    Page<Resource> findByCategoryAndIsActiveTrue(Resource.ResourceCategory category, Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE r.isActive = true AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Resource> searchActiveResources(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    List<Resource> findByUploadedByIdAndIsActiveTrueOrderByCreatedAtDesc(Long uploadedById);
    
    @Query("SELECT r FROM Resource r WHERE r.isActive = true ORDER BY r.downloadCount DESC")
    Page<Resource> findTopDownloadedResources(Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE r.isActive = true ORDER BY r.viewCount DESC")
    Page<Resource> findTopViewedResources(Pageable pageable);
}
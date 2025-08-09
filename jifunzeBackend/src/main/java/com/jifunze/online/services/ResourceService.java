package com.jifunze.online.services;

import com.jifunze.online.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ResourceService {
    
    Resource createResource(Resource resource);
    
    Optional<Resource> getResourceById(Long id);
    
    Page<Resource> getAllActiveResources(Pageable pageable);
    
    Page<Resource> getFreeResources(Pageable pageable);
    
    Page<Resource> getResourcesByCategory(Resource.ResourceCategory category, Pageable pageable);
    
    Page<Resource> searchResources(String searchTerm, Pageable pageable);
    
    List<Resource> getUserUploadedResources(Long userId);
    
    Resource updateResource(Long id, Resource resource);
    
    void deleteResource(Long id);
    
    void incrementDownloadCount(Long resourceId);
    
    void incrementViewCount(Long resourceId);
    
    Page<Resource> getTopDownloadedResources(Pageable pageable);
    
    Page<Resource> getTopViewedResources(Pageable pageable);
    
    boolean isResourceAccessible(Long userId, Long resourceId);
}
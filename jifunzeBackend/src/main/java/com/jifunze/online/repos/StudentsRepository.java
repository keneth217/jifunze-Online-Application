package com.jifunze.online.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jifunze.online.entity.Students;

public interface StudentsRepository extends JpaRepository<Students, Long> {
    Optional<Students> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
}
package com.datn.backendHN.repository;

import com.datn.backendHN.entity.BenhVienEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenhVienRepository extends JpaRepository<BenhVienEntity, Integer> {
    boolean existsByTenBenhVien(String tenBenhVien);
    boolean existsBySoDienThoai(String soDienThoai);
} 
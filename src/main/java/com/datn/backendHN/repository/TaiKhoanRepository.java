package com.datn.backendHN.repository;

import com.datn.backendHN.entity.TaiKhoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoanEntity, Integer> {
    Optional<TaiKhoanEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<TaiKhoanEntity> findByVaiTro(String vaiTro);
} 
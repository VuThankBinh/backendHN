package com.datn.backendHN.service;

import com.datn.backendHN.dto.BenhVienDto;
import com.datn.backendHN.entity.BenhVienEntity;
import com.datn.backendHN.entity.TaiKhoanEntity;
import com.datn.backendHN.repository.BenhVienRepository;
import com.datn.backendHN.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BenhVienService {

    @Autowired
    private BenhVienRepository benhVienRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    public List<BenhVienDto> getAllBenhVien() {
        return benhVienRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BenhVienDto getBenhVienById(Integer id) {
        BenhVienEntity benhVien = benhVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh viện với ID: " + id));
        return convertToDto(benhVien);
    }

    @Transactional
    public BenhVienDto createBenhVien(BenhVienDto dto) {
        if (benhVienRepository.existsByTenBenhVien(dto.getTenBenhVien())) {
            throw new RuntimeException("Tên bệnh viện đã tồn tại");
        }
        if (benhVienRepository.existsBySoDienThoai(dto.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        TaiKhoanEntity taiKhoan = taiKhoanRepository.findById(dto.getIdTaiKhoan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với ID: " + dto.getIdTaiKhoan()));

        BenhVienEntity benhVien = new BenhVienEntity();
        benhVien.setTenBenhVien(dto.getTenBenhVien());
        benhVien.setDiaChi(dto.getDiaChi());
        benhVien.setSoDienThoai(dto.getSoDienThoai());
        benhVien.setTaiKhoan(taiKhoan);

        BenhVienEntity savedBenhVien = benhVienRepository.save(benhVien);
        return convertToDto(savedBenhVien);
    }

    @Transactional
    public BenhVienDto updateBenhVien(Integer id, BenhVienDto dto) {
        BenhVienEntity benhVien = benhVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh viện với ID: " + id));

        if (!benhVien.getTenBenhVien().equals(dto.getTenBenhVien()) && 
            benhVienRepository.existsByTenBenhVien(dto.getTenBenhVien())) {
            throw new RuntimeException("Tên bệnh viện đã tồn tại");
        }

        if (!benhVien.getSoDienThoai().equals(dto.getSoDienThoai()) && 
            benhVienRepository.existsBySoDienThoai(dto.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        TaiKhoanEntity taiKhoan = taiKhoanRepository.findById(dto.getIdTaiKhoan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với ID: " + dto.getIdTaiKhoan()));

        benhVien.setTenBenhVien(dto.getTenBenhVien());
        benhVien.setDiaChi(dto.getDiaChi());
        benhVien.setSoDienThoai(dto.getSoDienThoai());
        benhVien.setTaiKhoan(taiKhoan);

        BenhVienEntity updatedBenhVien = benhVienRepository.save(benhVien);
        return convertToDto(updatedBenhVien);
    }

    @Transactional
    public void deleteBenhVien(Integer id) {
        if (!benhVienRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bệnh viện với ID: " + id);
        }
        benhVienRepository.deleteById(id);
    }

    private BenhVienDto convertToDto(BenhVienEntity entity) {
        BenhVienDto dto = new BenhVienDto();
        dto.setId(entity.getId());
        dto.setTenBenhVien(entity.getTenBenhVien());
        dto.setDiaChi(entity.getDiaChi());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setIdTaiKhoan(entity.getTaiKhoan().getId());
        return dto;
    }
} 
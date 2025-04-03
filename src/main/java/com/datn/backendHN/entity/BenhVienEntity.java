package com.datn.backendHN.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "BenhVien")
@Data
public class BenhVienEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên bệnh viện không được để trống")
    @Column(name = "ten_benh_vien", nullable = false, unique = true )
    private String tenBenhVien;

    @Column(name = "dia_chi", columnDefinition = "geography", unique = true )
    private String diaChi;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Column(name = "so_dien_thoai")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String soDienThoai;

    @ManyToOne
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoanEntity taiKhoan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }
} 
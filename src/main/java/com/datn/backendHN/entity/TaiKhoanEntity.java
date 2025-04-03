package com.datn.backendHN.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "taikhoan")
@Data
public class TaiKhoanEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Email không được để trống")
    @Column(name = "email", nullable = false, unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "mat_khau", nullable = false)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", 
            message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ cái và số")
    private String matKhau;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Column(name = "so_dien_thoai")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String soDienThoai;

    @NotEmpty(message = "Vai trò không được để trống")
    @Column(name = "vai_tro")
    private String vaiTro;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @NotBlank(message = "Tên không được để trống")
    @Column(name = "ten")
    private String ten;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }
} 
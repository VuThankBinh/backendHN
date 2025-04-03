package com.datn.backendHN.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BenhVienDto {
    private Integer id;

    @NotBlank(message = "Tên bệnh viện không được để trống")
    private String tenBenhVien;

    private String diaChi;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String soDienThoai;

    private Integer idTaiKhoan;
} 
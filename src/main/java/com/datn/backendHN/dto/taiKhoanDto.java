package com.datn.backendHN.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class taiKhoanDto {
    private Integer id;

    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "^.{8,}$", message = "Mật khẩu phải có ít nhất 8 ký tự")    
    private String matKhau;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String soDienThoai;

    private String vaiTro;
    private LocalDateTime ngayTao;

    @NotBlank(message = "Tên không được để trống")
    private String ten;
}

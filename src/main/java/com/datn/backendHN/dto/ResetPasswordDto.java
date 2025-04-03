package com.datn.backendHN.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String email;
    private String matKhauMoi;
    private String xacNhanMatKhauMoi;
}
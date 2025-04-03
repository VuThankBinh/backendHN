package com.datn.backendHN.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String matKhauCu;
    private String matKhauMoi;
    private String xacNhanMatKhauMoi;
}
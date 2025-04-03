package com.datn.backendHN.controller;

import com.datn.backendHN.dto.taiKhoanDto;
import com.datn.backendHN.dto.ChangePasswordDto;
import com.datn.backendHN.dto.ResetPasswordDto;
import com.datn.backendHN.entity.ResponseObject;
import com.datn.backendHN.entity.TaiKhoanEntity;
import com.datn.backendHN.exception.NotFoundException;
import com.datn.backendHN.exception.ValidationException;
import com.datn.backendHN.service.TaiKhoanService;
import com.datn.backendHN.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/taikhoan")
@CrossOrigin(origins = "*")
public class TaiKhoanController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody taiKhoanDto dto) {
        try {
            if(!dto.getVaiTro().equals("benh_nhan")){
                throw new ValidationException("Vai trò không hợp lệ. Các vai trò được phép: benh_nhan");
            }
            taiKhoanDto result = taiKhoanService.register(dto);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody taiKhoanDto dto) {
        try {
            Map<String, Object> result = taiKhoanService.login(dto.getEmail(), dto.getMatKhau());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public taiKhoanDto getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtils.getEmailFromJwtToken(token);
                
                // Tạo DTO với thông tin từ token
                taiKhoanDto taiKhoanDTO = new taiKhoanDto();
                TaiKhoanEntity taiKhoan = taiKhoanService.findByEmail(email);
                taiKhoanDTO.setEmail(taiKhoan.getEmail());
                taiKhoanDTO.setVaiTro(taiKhoan.getVaiTro());
                taiKhoanDTO.setId(taiKhoan.getId());
                System.out.println("taiKhoanDTO: " + taiKhoanDTO);
                return taiKhoanDTO;
            }
            throw new NotFoundException("Token không hợp lệ hoặc không tồn tại");
        } catch (Exception e) {
            throw new NotFoundException("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody ChangePasswordDto dto) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtils.getEmailFromJwtToken(token);
                TaiKhoanEntity taiKhoan = taiKhoanService.findByEmail(email);
                
                taiKhoanService.changePassword(taiKhoan.getId(), dto);
                return ResponseEntity.ok().body("Đổi mật khẩu thành công");
            }
            throw new NotFoundException("Token không hợp lệ hoặc không tồn tại");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto) {
        try {
            taiKhoanService.resetPassword(dto);
            return ResponseEntity.ok().body("Đặt lại mật khẩu thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<taiKhoanDto>> getTaiKhoanById(@PathVariable Integer id) {
        try {
            taiKhoanDto taiKhoan = taiKhoanService.getTaiKhoanById(id);
            if (taiKhoan == null) {
                return ResponseEntity.badRequest().body(new ResponseObject<taiKhoanDto>(HttpStatus.BAD_REQUEST, "TaiKhoan not found", null));
            }
            return ResponseEntity.ok(new ResponseObject<taiKhoanDto>(HttpStatus.OK, "Success", taiKhoan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject<taiKhoanDto>(HttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody taiKhoanDto dto) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtUtils.getEmailFromJwtToken(token);
                TaiKhoanEntity taiKhoan = taiKhoanService.findByEmail(email);
                
                taiKhoanDto updatedProfile = taiKhoanService.updateProfile(taiKhoan.getId(), dto);
                return ResponseEntity.ok(updatedProfile);
            }
            throw new NotFoundException("Token không hợp lệ hoặc không tồn tại");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
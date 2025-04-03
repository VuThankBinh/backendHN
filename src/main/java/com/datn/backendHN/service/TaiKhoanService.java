package com.datn.backendHN.service;

import com.datn.backendHN.dto.taiKhoanDto;
import com.datn.backendHN.dto.ChangePasswordDto;
import com.datn.backendHN.dto.ResetPasswordDto;
import com.datn.backendHN.entity.TaiKhoanEntity;
import com.datn.backendHN.exception.EmailExistsException;
import com.datn.backendHN.exception.ValidationException;
import com.datn.backendHN.repository.TaiKhoanRepository;
import com.datn.backendHN.security.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TaiKhoanService {
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final List<String> VALID_VAI_TRO = Arrays.asList(
        "benh_nhan", "duoc_si", "benh_vien", "le_tan", "bac_si", "admin"
    );

    @Transactional
    public taiKhoanDto register(taiKhoanDto dto) {
        if (taiKhoanRepository.existsByEmail(dto.getEmail())) {
            throw new EmailExistsException("Email đã được sử dụng");
        }
        if (dto.getVaiTro() == null || !VALID_VAI_TRO.contains(dto.getVaiTro())) {
            throw new ValidationException("Vai trò không hợp lệ. Các vai trò được phép: " + String.join(", ", VALID_VAI_TRO));
        }
        String vaiTro = dto.getVaiTro() != null ? dto.getVaiTro() : "benh_nhan";
        if (!VALID_VAI_TRO.contains(vaiTro)) {
            throw new ValidationException("Vai trò không hợp lệ. Các vai trò được phép: " + String.join(", ", VALID_VAI_TRO));
        }

        TaiKhoanEntity taiKhoan = new TaiKhoanEntity();
        taiKhoan.setEmail(dto.getEmail());
        taiKhoan.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        taiKhoan.setSoDienThoai(dto.getSoDienThoai());
        taiKhoan.setVaiTro(vaiTro);
        taiKhoan.setTen(dto.getTen());

        TaiKhoanEntity savedTaiKhoan = taiKhoanRepository.save(taiKhoan);
        
        return convertToDto(savedTaiKhoan);
    }

    public Map<String, Object> login(String email, String matKhau) {
        TaiKhoanEntity taiKhoan = taiKhoanRepository.findByEmail(email)
            .orElseThrow(() -> new ValidationException("Email hoặc mật khẩu không chính xác. Vui lòng kiểm tra lại."));

        if (!passwordEncoder.matches(matKhau, taiKhoan.getMatKhau())) {
            throw new ValidationException("Email hoặc mật khẩu không chính xác. Vui lòng kiểm tra lại.");
        }

        // Tạo JWT token
        String token = jwtUtils.generateJwtToken(taiKhoan);
        
        // Lưu token vào Redis với key là "token:userId" và thời gian sống là 24h
        redisTemplate.opsForValue().set(
            "token:" + taiKhoan.getId(), 
            token,
            24, 
            TimeUnit.HOURS
        );

        // Trả về thông tin user và token
        Map<String, Object> response = new HashMap<>();
        response.put("user", convertToDto(taiKhoan));
        response.put("token", token);
        
        return response;
    }

    public TaiKhoanEntity findByEmail(String email) {
        return taiKhoanRepository.findByEmail(email)
            .orElseThrow(() -> new ValidationException(
                String.format("Không tìm thấy tài khoản với email: %s. Vui lòng kiểm tra lại email hoặc đăng ký tài khoản mới.", 
                    email
                )
            ));
    }

    @Transactional
    public void changePassword(Integer userId, ChangePasswordDto dto) {
        TaiKhoanEntity taiKhoan = taiKhoanRepository.findById(userId)
            .orElseThrow(() -> new ValidationException("Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(dto.getMatKhauCu(), taiKhoan.getMatKhau())) {
            throw new ValidationException("Mật khẩu cũ không chính xác");
        }

        if (!dto.getMatKhauMoi().equals(dto.getXacNhanMatKhauMoi())) {
            throw new ValidationException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        taiKhoan.setMatKhau(passwordEncoder.encode(dto.getMatKhauMoi()));
        taiKhoanRepository.save(taiKhoan);
    }

    @Transactional
    public void resetPassword(ResetPasswordDto dto) {
        if (!dto.getMatKhauMoi().equals(dto.getXacNhanMatKhauMoi())) {
            throw new ValidationException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        TaiKhoanEntity taiKhoan = findByEmail(dto.getEmail());
        taiKhoan.setMatKhau(passwordEncoder.encode(dto.getMatKhauMoi()));
        taiKhoanRepository.save(taiKhoan);
    }

    @Transactional
    public taiKhoanDto updateProfile(Integer userId, taiKhoanDto dto) {
        TaiKhoanEntity taiKhoan = taiKhoanRepository.findById(userId)
            .orElseThrow(() -> new ValidationException("Không tìm thấy tài khoản"));

        // Kiểm tra email mới có trùng với email của tài khoản khác không
        if (!taiKhoan.getEmail().equals(dto.getEmail()) && 
            taiKhoanRepository.existsByEmail(dto.getEmail())) {
            throw new EmailExistsException("Email đã được sử dụng");
        }

        // Cập nhật thông tin
        taiKhoan.setEmail(dto.getEmail());
        taiKhoan.setSoDienThoai(dto.getSoDienThoai());
        taiKhoan.setTen(dto.getTen());

        TaiKhoanEntity updatedTaiKhoan = taiKhoanRepository.save(taiKhoan);
        return convertToDto(updatedTaiKhoan);
    }

    // Các phương thức mới cho quản lý tài khoản bệnh viện
    public List<taiKhoanDto> getAllTaiKhoanByVaiTro(String vaiTro) {
        return taiKhoanRepository.findByVaiTro(vaiTro).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public taiKhoanDto getTaiKhoanById(Integer id) {
        TaiKhoanEntity taiKhoan = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Không tìm thấy tài khoản với ID: " + id));
        return convertToDto(taiKhoan);
    }
    

    @Transactional
    public void deleteTaiKhoan(Integer id) {
        if (!taiKhoanRepository.existsById(id)) {
            throw new ValidationException("Không tìm thấy tài khoản với ID: " + id);
        }
        taiKhoanRepository.deleteById(id);
    }

    private taiKhoanDto convertToDto(TaiKhoanEntity taiKhoan) {
        taiKhoanDto dto = new taiKhoanDto();
        dto.setId(taiKhoan.getId());
        dto.setEmail(taiKhoan.getEmail());
        dto.setSoDienThoai(taiKhoan.getSoDienThoai());
        dto.setVaiTro(taiKhoan.getVaiTro());
        dto.setNgayTao(taiKhoan.getNgayTao());
        dto.setTen(taiKhoan.getTen());
        return dto;
    }
} 
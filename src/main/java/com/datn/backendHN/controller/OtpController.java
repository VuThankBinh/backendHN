package com.datn.backendHN.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.datn.backendHN.dto.OtpRequest;
import org.springframework.http.ResponseEntity;
import com.datn.backendHN.service.otpService;
import com.datn.backendHN.entity.ResponseObject;
import com.datn.backendHN.dto.OtpValidationRequest;

// import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private otpService otpService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/send")
    public ResponseEntity<ResponseObject<String>> sendOtp(@RequestBody OtpRequest request) {
        try {
            otpService.generateAndSendOtp(request.getEmail());
            return ResponseEntity.ok(
                    new ResponseObject(
                            HttpStatus.OK,
                            "OTP đã được gửi đến email của bạn",
                            ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            new ResponseObject(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Không thể gửi OTP",
                                    ""));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/sendOTPreset")
    public ResponseEntity<ResponseObject<String>> sendOtpResetPass(@RequestBody OtpRequest request) {
        try {
            otpService.generateAndSendOtpReset(request.getEmail());
            return ResponseEntity.ok(
                    new ResponseObject(
                            HttpStatus.OK,
                            "OTP đã được gửi đến email của bạn",
                            ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            new ResponseObject(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Không thể gửi OTP đến email của bạn",
                                    ""));
        }
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/verify")
    public ResponseEntity<ResponseObject<String>> verifyOtp(@RequestBody OtpValidationRequest request) {
        boolean isValid = otpService.validateOtp(request.getEmail(), request.getOtp());

        if (isValid) {
            return ResponseEntity.ok(
                    new ResponseObject(
                            HttpStatus.OK,
                            "Xác thực OTP thành công",
                            ""));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST,
                            "OTP không hợp lệ hoặc đã hết hạn",
                            ""));
        }
    }
}
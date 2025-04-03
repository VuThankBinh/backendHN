package com.datn.backendHN.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.datn.backendHN.repository.TaiKhoanRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Random;

@Service
@Slf4j
public class otpService {

    
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Autowired
    private emailService emailService;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    public void generateAndSendOtp(String email) {
        String otp = generateOtp();
        log.info("Generating OTP for email: {}", email + " " + otp);
        
        try {
            // Check Redis connection before
            boolean isRedisConnected = checkRedisConnection();
            if (!isRedisConnected) {
                log.error("Can not connect to Redis server");
                throw new RuntimeException("System is temporarily unavailable, please try again later");
            }

            // Send email before
            emailService.sendOtpEmail(email, otp);
            log.info("Sent OTP email successfully");

            // Save OTP to Redis
            redisTemplate.opsForValue().set(
                email,
                otp,
                Duration.ofMinutes(1)
            );
            
            log.info("Saved OTP to Redis successfully");
            
        } catch (Exception e) {
            log.error("Error while processing OTP: {}", e.getMessage(), e);
            throw new RuntimeException("Can not send OTP: " + e.getMessage());
        }
    }
    
    public void generateAndSendOtpReset(String email) {
        String otp = generateOtp();
        log.info("Generating OTP for email: {}", email + " " + otp);
        if (!taiKhoanRepository.existsByEmail(email)) {
            log.info("email: {}", email + " don't exist");
            throw new RuntimeException("Email don't exist");
        }
        else{
            try {
                // Check Redis connection before
                boolean isRedisConnected = checkRedisConnection();
                if (!isRedisConnected) {
                    log.error("Can not connect to Redis server");
                    throw new RuntimeException("System is temporarily unavailable, please try again later");
                }
    
                // Send email before
                emailService.sendOtpEmail(email, otp);
                log.info("Sent OTP email successfully");
    
                // Save OTP to Redis
                redisTemplate.opsForValue().set(
                    email,
                    otp,
                    Duration.ofMinutes(1)
                );
                
                log.info("Saved OTP to Redis successfully");
                
            } catch (Exception e) {
                log.error("Error while processing OTP: {}", e.getMessage(), e);
                throw new RuntimeException("Can not send OTP: " + e.getMessage());
            }
        }
    }
    
    public boolean validateOtp(String email, String otp) {
        try {
            String storedOtp = redisTemplate.opsForValue().get(email);
            log.debug("Validating OTP for email: {}. Stored OTP: {}", email, storedOtp);
            
            if (storedOtp == null) {
                return false;
            }
            
            boolean isValid = storedOtp.equals(otp);
            
            if (isValid) {
                redisTemplate.delete(email);
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("Error while validating OTP: ", e);
            return false;
        }
    }
    
    private String generateOtp() {
        Random random = new Random();
        int number = random.nextInt(999999);
        return String.format("%06d", number);
    }

    @SuppressWarnings("null")
    private boolean checkRedisConnection() {
        try {
            return redisTemplate.getConnectionFactory().getConnection().ping() != null;
        } catch (Exception e) {
            log.error("Error while checking Redis connection: {}", e.getMessage());
            return false;
        }
    }
} 
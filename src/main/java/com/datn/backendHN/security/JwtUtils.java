package com.datn.backendHN.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.datn.backendHN.entity.TaiKhoanEntity;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
@Component
public class JwtUtils {
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Tạo key an toàn

    public String generateJwtToken(TaiKhoanEntity taiKhoan) {
        
        return Jwts.builder()
                .setSubject(taiKhoan.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getVaiTroFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("vaiTro", String.class);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 
package hello.hellospring.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey key;

    // 객체 생성 후 key 초기화
    @PostConstruct
    public void init() {
        String secretKey = "bXlTdXBlclNlY3JldEtleUhlcmVJc1ZlcnlMb25nU3VwZXJMb25nS2V5";
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성
    public String generateAccessToken(String email) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15분
                .signWith(key, SignatureAlgorithm.HS256); // SecretKey 객체 사용

        String accesstoken = builder.compact();
        return "Bearer " + accesstoken;
    }

    // Refresh Token 생성
    public String generateRefreshToken(String email) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7일
                .signWith(key, SignatureAlgorithm.HS256);

        String refreshToken = builder.compact();
        return "Bearer " + refreshToken;
    }

    // 토큰에서 이메일 추출
    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // SecretKey 객체 사용
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 토큰이 만료되었는지 확인
    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    // 토큰의 만료일자 추출
    private Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }
}

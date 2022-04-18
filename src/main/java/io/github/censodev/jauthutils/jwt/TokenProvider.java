package io.github.censodev.jauthutils.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TokenProvider {
    @Builder.Default
    private String header = "Authorization";

    @Builder.Default
    private String prefix = "Bearer ";

    @Builder.Default
    private int expiration = 86_400_000;

    @Builder.Default
    private String secret = "qwertyuiopasdfghjklzxcvbnm1!2@3#4$5%6^7&8*9(0)-_=+";

    @Builder.Default
    private ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    public <T extends Credentials> String generateToken(T credentials) throws JsonProcessingException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(String.valueOf(credentials.getSubject()))
                .claim("credentials", mapper.writeValueAsString(credentials))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public <T extends Credentials> T getCredentials(String token, Class<T> tClass) throws IOException {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return mapper.readValue(String.valueOf(claims.get("credentials")), tClass);
    }

    public void validateToken(String token) throws
            MalformedJwtException,
            ExpiredJwtException,
            UnsupportedJwtException,
            IllegalArgumentException,
            SignatureException {
        Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
    }
}

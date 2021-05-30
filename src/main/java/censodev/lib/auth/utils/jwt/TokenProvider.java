package censodev.lib.auth.utils.jwt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@Getter
public class TokenProvider {
    private final String header = "Authorization";
    private final String prefix = "Bearer ";
    private final int expiration = 86_400_000;
    private final String secret = "jalskdjlakjdlkajsdlkjsalkdjsalkdjlksajdlksajdlksajdlkjsalkdjaslkdjlksajdlksajdl";

    public <T extends Credentials> String generateToken(T credentials) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(String.valueOf(credentials.getSubject()))
                .claim("credentials", credentials)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public <T extends Credentials> T getCredentials(String token, Class<T> tClass) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.convertValue(claims.get("credentials"), tClass);
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

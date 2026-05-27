package es.daw.pixaymvc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${JWT_SECRET}")
    private String secret;

    @Value("${JWT_EXPIRATION}")
    private int expiration;

    /**
     * Devuelve la clave secreta usada para firmar y validar tokens.
     * @return
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un JWT para el usuario autenticado.
     * - Incluye los roles del usuario en la sección claims.
     * - Expira después de 1 hora (1000 * 60 * 60 ms).
     * - Firma el token con la clave secreta SECRET_KEY usando el algoritmo HS256.
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        claims.put("roles", roles);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Verifica si el token es válido comparando:
     *
     * Si el username en el token es el mismo que en UserDetails.
     * Si el token no ha expirado.
     * @param token
     * @param userDetails
     * @return
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        if (userDetails == null) {
            return (username != null) && !isTokenExpired(token);
        }
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     *  Extrae la fecha de expiración del token y la compara con la fecha actual.
     *  Si la fecha de expiración ya pasó, el token es inválido.
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Verifica y decodifica el token JWT.
     * Extrae todos los datos (claims), incluyendo:
     *
     * sub → nombre de usuario
     * roles → lista de roles
     * iat → fecha de emisión
     * exp → fecha de expiración
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
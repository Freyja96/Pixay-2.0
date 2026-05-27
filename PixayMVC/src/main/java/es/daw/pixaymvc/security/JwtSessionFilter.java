package es.daw.pixaymvc.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtSessionFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = (String) request.getSession().getAttribute("token");

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtService.isTokenValid(token, null)) {
                    String username = jwtService.extractUsername(token);

                    List<String> roles = jwtService.extractClaim(token, claims -> claims.get("roles", List.class));

                    var authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                request.getSession().removeAttribute("token");
            }
        }
        filterChain.doFilter(request, response);
    }
}
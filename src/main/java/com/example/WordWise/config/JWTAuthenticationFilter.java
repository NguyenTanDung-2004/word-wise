package com.example.WordWise.config;

import com.example.WordWise.enums.PermissionEnum;
import com.example.WordWise.enums.RoleEnum;
import com.example.WordWise.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = this.jwtUtils.getTokenFromHeader(authHeader);

            if (!this.jwtUtils.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            Map<String, Object> decodedMap = this.jwtUtils.decodeJWT(token);

            String role = (String) decodedMap.get("role");
            List<String> permissions = (List<String>) decodedMap.get("permission");

            List<GrantedAuthority> authorities = new ArrayList<>();

            // Add role
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));  // e.g., ROLE_ADMIN

            // Add permissions
            for (String permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission)); // e.g., PERM_VIEW_USER
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(decodedMap, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}

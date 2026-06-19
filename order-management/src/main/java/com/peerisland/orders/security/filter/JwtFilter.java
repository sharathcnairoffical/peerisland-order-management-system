package com.peerisland.orders.security.filter;


import com.peerisland.orders.cache.InMemoryCacheService;
import com.peerisland.orders.security.dto.SecurityProperties;
import com.peerisland.orders.security.service.JWTService;
import com.peerisland.orders.security.service.MyUserDetailsService;
import com.peerisland.orders.utility.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final MyUserDetailsService userDetailsService;
    private final SecurityProperties securityProperties;
    private final InMemoryCacheService inMemoryCacheService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final List<String> PUBLIC_PATHS = List.of(
            "/v1/public/**", "/v1/menu/**", "/actuator/**", "/favicon.ico",
            "/v1/public/login/*", "/session/login"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return PUBLIC_PATHS.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path))
                || securityProperties.allowedPublicApis().stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith(Constants.BEARER_LITERAL)) {
            token = authHeader.replace(Constants.BEARER_LITERAL, StringUtils.EMPTY);
            if (isAccessTokenBlacklisted(token)) {
                log.warn("Rejected blacklisted access token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            username = jwtService.extractUserName(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, username)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAccessTokenBlacklisted(String token) {
        String hashedToken = DigestUtils.sha256Hex(token);
        return inMemoryCacheService.contains(Constants.BLACKLISTED_ACCESS_TOKEN_PREFIX.concat(hashedToken));
    }
}

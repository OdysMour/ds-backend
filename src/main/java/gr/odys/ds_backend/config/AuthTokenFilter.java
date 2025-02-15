package gr.odys.ds_backend.config;

import gr.odys.ds_backend.service.UserDetailsServiceImpl;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_LENGTH = BEARER_PREFIX.length();

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                  @Nonnull HttpServletResponse response,
                                  @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (jwt != null) {
                if (jwtUtils.validateJwtToken(jwt)) {
                    processValidToken(jwt, request);
                } else {
                    logger.warn("Invalid JWT token received from IP: {}", request.getRemoteAddr());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Invalid token\"}");
                    return;
                }
            }
        } catch (UsernameNotFoundException e) {
            logger.error("User not found", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"User not found\"}");
            return;
        } catch (Exception e) {
            logger.error("Authentication error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication error\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void processValidToken(String jwt, HttpServletRequest request) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities() != null ? userDetails.getAuthorities() : Collections.emptyList()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
            String token = headerAuth.substring(BEARER_LENGTH);
            if (StringUtils.hasText(token)) {
                return token;
            }
        }
        return null;
    }
}
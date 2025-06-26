package com.deepak.management.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final String AUTH_HEADER = "Authorization";
  private static final String TOKEN_PREFIX = "Bearer ";

  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailsService customUserDetailsService;
  private final com.deepak.management.service.BlacklistedAccessTokenService
      blacklistedAccessTokenService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = getJwtFromRequest(request);
      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        // Check if token is blacklisted
        if (blacklistedAccessTokenService.isTokenBlacklisted(jwt)) {
          logger.warn("Attempt to use blacklisted JWT token");
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Blacklisted JWT token");
          return;
        }
        String username = tokenProvider.getUsernameFromToken(jwt);

        // Only proceed if user is not already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

          if (userDetails instanceof CustomUserDetails customUserDetails) {
            // Create authentication object
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }
      }
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token: {}", ex.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
      return;
    } catch (UnsupportedJwtException
        | MalformedJwtException
        | SecurityException
        | SignatureException ex) {
      logger.error("Invalid JWT token: {}", ex.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
      return;
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty: {}", ex.getMessage());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT claims string is empty");
      return;
    } catch (Exception ex) {
      logger.error(
          "Could not set user authentication in security context: {}", ex.getMessage(), ex);
      response.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing JWT token");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTH_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(TOKEN_PREFIX.length());
    }
    return null;
  }
}

package com.example.demo.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		if(request.getRequestURI().contains("/login")) {
			filterChain.doFilter(request, response);
			
		}
		else {
			String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if(StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
				try {
					String token = authorizationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm.HMAC512("secret");
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decoded = verifier.verify(token);
					String username= decoded.getSubject();
					log.info("Token for {} is valid", username);
					String[] claims = decoded.getClaim("roles").asArray(String.class);
					List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
					Stream.of(claims).forEach(role->{
						authorities.add(new SimpleGrantedAuthority(role));
					});
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authToken);
					filterChain.doFilter(request, response);
					
				} catch (Exception e) {
					log.error("Exception {}",e);
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
				}
			} else {
				filterChain.doFilter(request, response);
			}
			
			
		}
		
	}

}

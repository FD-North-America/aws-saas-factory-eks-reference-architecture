package com.amazonaws.saas.eks.auth;

import com.amazonaws.saas.eks.error.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthFilter awsCognitoJwtAuthFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Because we don’t use classic web so disable CSRF and no session management
		// needed
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.headers().cacheControl();

		http.authorizeRequests()
				.antMatchers("**/orders/health").permitAll()
				.antMatchers("**/orders/**").authenticated();

		http.exceptionHandling()
				.authenticationEntryPoint(
						(request, response, ex) -> {
							ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
							ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
							response.setContentType("application/json");
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.getOutputStream().println(om.writeValueAsString(apiError));
						});

		http.addFilterBefore(awsCognitoJwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		http.cors();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}

package com.team1.etapigateway.config;

import com.team1.etapigateway.filter.JwtGatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtGatewayFilter jwtGatewayFilter;

    public SecurityConfig(JwtGatewayFilter jwtGatewayFilter) {
        this.jwtGatewayFilter = jwtGatewayFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .cors(cors -> cors.configurationSource(corsConfig()))
                .authorizeHttpRequests(auth->
                                auth.requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
                                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtGatewayFilter, UsernamePasswordAuthenticationFilter.class);

//        http
//                .csrf().disable()
//                .cors().configurationSource(corsConfigurationSource())
//                .and()
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtGatewayFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfig() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

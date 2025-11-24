package com.buixuanphat.spot_on.configuration;

import com.buixuanphat.spot_on.exception.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.signerKey}")
    String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        String[] permitAll = {"/users/register", "/auth/log-in", "/organizers/register"};

        http.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST ,permitAll).permitAll()
                        .anyRequest().authenticated());

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()).accessDeniedHandler((new JwtAccessDeniedHandler())));

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec spec = new SecretKeySpec(signerKey.getBytes(), "HS256");

        return NimbusJwtDecoder.withSecretKey(spec).macAlgorithm(MacAlgorithm.HS256).build();
    }
}

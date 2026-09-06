package com.SecurityApp.config;

import com.SecurityApp.filters.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private  final JwtAuthFilter jwtAuthFilter;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/auth/**").permitAll()
//                        .requestMatchers("/posts/**").hasRole("ADMIN")
//                                .requestMatchers("/posts/**").authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

//                .formLogin(Customizer.withDefaults());

        return httpSecurity.build();
    }

    //AuthenticationManager
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config){
       return  config.getAuthenticationManager();
    }
//    @Bean
//    UserDetailsService myInMemoryUserDetailsService(){
//        UserDetails normalUser= User.withUsername("Nitu").
//                password(passwordEncoder().encode("Nitu@123"))
//                .roles("USER").build();
//        UserDetails adminUser=User.withUsername("Riya")
//                .password(passwordEncoder().encode("Riya@123"))
//                .roles("ADMIN").build();
//
//        return  new InMemoryUserDetailsManager(normalUser,adminUser);
//    }


}

package com.SecurityApp.config;

import com.SecurityApp.entities.enums.Role;
import com.SecurityApp.filters.JwtAuthFilter;
import com.SecurityApp.handlers.oauthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.SecurityApp.entities.enums.Role.ADMIN;
import static com.SecurityApp.entities.enums.Role.CREATOR;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
        private final JwtAuthFilter jwtAuthFilter;
        private  final oauthSuccessHandler oauthSuccessHandler;
        private  static  final  String [] publicRoute=
                {
                        "/error", "/auth/**","/home.html"
                };

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                httpSecurity
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(publicRoute).permitAll()
                                                // .requestMatchers("/posts/**").hasRole("ADMIN")
                                                // .requestMatchers("/posts/**").authenticated()
//                                               .requestMatchers("/posts/**").hasRole(ADMIN.name())
                                        .requestMatchers(HttpMethod.GET,"/posts/**").permitAll()
                                        .requestMatchers(HttpMethod.POST,"/posts/**")
                                        .hasAnyRole(ADMIN.name(), CREATOR.name())
                                                .anyRequest().authenticated())
                                               .csrf(csrfConfig -> csrfConfig.disable())
                                               .sessionManagement(sessionConfig -> sessionConfig
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                                .oauth2Login(oauthConfig -> oauthConfig
                                                        .failureUrl("/login?error=true")
                                                        .successHandler(oauthSuccessHandler)
                                                );

                // .formLogin(Customizer.withDefaults());

                return httpSecurity.build();
        }

        // AuthenticationManager
        @Bean
        AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
                return config.getAuthenticationManager();
        }
        // @Bean
        // UserDetailsService myInMemoryUserDetailsService(){
        // UserDetails normalUser= User.withUsername("Nitu").
        // password(passwordEncoder().encode("Nitu@123"))
        // .roles("USER").build();
        // UserDetails adminUser=User.withUsername("Riya")
        // .password(passwordEncoder().encode("Riya@123"))
        // .roles("ADMIN").build();
        //
        // return new InMemoryUserDetailsManager(normalUser,adminUser);
        // }

}

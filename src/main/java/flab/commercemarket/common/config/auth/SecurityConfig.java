package flab.commercemarket.common.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(a -> a
                        .antMatchers("/").permitAll()
                        .antMatchers("/login").permitAll()
                        .antMatchers(HttpMethod.POST, "/products/**").hasRole("USER")
                        .antMatchers(HttpMethod.PATCH, "/products").hasRole("USER")
                        .antMatchers(HttpMethod.DELETE, "/products/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .antMatchers("/carts/**").hasRole("USER")
                        .antMatchers("/wishes/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
                )
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);
        return http.build();
    }
}

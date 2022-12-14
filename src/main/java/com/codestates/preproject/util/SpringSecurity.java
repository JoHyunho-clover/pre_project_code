package com.codestates.preproject.util;

import com.codestates.preproject.jwt.JwtAccessDeniedHandler;
import com.codestates.preproject.jwt.JwtAuthenticationEntryPoint;
import com.codestates.preproject.jwt.JwtSecurityConfig;
import com.codestates.preproject.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurity extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //.httpBasic().disable()
        //                .cors().configurationSource(corsConfigurationSource())
        //                .and()
        http.cors().and()
                .csrf().disable()
                .formLogin().disable()
                .headers().frameOptions().disable()
                .and()
                // exception handling ??? ??? ????????? ?????? ???????????? ??????
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // h2-console ??? ?????? ????????? ??????
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // ??????????????? ??????????????? ????????? ??????
                // ???????????? ????????? ???????????? ?????? ????????? ?????? ????????? Stateless ??? ??????
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // ?????????, ???????????? API, ?????????(header???) ??? ????????? ?????? ???????????? ????????? ???????????? ????????? permitAll ??????
                .and()
                .authorizeRequests()
                //.antMatchers("/auth/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() //users/logout
                .mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/questions").permitAll() //users/logout
                .antMatchers("/questions/{questionI}").permitAll()
                .antMatchers("/users/login").permitAll()
                .antMatchers("/users/sign").permitAll()
                .antMatchers("/users/reissue").permitAll()
                .antMatchers("/questions").permitAll()
                .anyRequest().authenticated()   // ????????? API ??? ?????? ?????? ??????

                // JwtFilter ??? addFilterBefore ??? ???????????? JwtSecurityConfig ???????????? ??????
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}

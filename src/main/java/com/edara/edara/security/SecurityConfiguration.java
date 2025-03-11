

package com.edara.edara.security;

import com.edara.edara.security.exceptionHandling.CustomAccessDeniedHandler;
import com.edara.edara.security.exceptionHandling.CustomBasicAuthenticationEntryPoint;
import com.edara.edara.security.filter.CsrfCookieFilter;
import com.edara.edara.security.filter.JWTTokenGeneratorFilter;
import com.edara.edara.security.filter.JWTTokenValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        // Add Swagger Editor Origin
                        config.setAllowedOrigins(Collections.singletonList("https://editor-next.swagger.io"));
                        // You can allow other origins as needed
                        // config.setAllowedOrigins(Arrays.asList("https://editor-next.swagger.io", "http://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);  // Cache CORS pre-flight response
                        return config;
}
                }))
                .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers( "/users/register","/auth/login")
                        .ignoringRequestMatchers( "/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
//                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
//                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
                //.requiresChannel(rcc -> rcc.anyRequest().requiresSecure()) // Only HTTPS
                .authorizeHttpRequests((requests) -> requests


                        .requestMatchers("/auth/login", "/users/register").permitAll()

                        //.requestMatchers(PathRequest.toH2Console()).permitAll()

                        .requestMatchers("/users/edit-profile/{userId}").hasAuthority("USER")
                        .requestMatchers("/users/get/{userId}").hasAuthority("USER")

                        .requestMatchers("/projects/add").hasAuthority("USER")
                        .requestMatchers("/projects/update/{projectId}").hasAuthority("USER")
                        .requestMatchers("/projects/delete/{projectId}").hasAuthority("USER")
                        .requestMatchers("/projects/get/{projectId}").hasAuthority("USER")

                        .requestMatchers("/titles/add/{projectId}").hasAuthority("USER")
                        .requestMatchers("/titles/update/{titleId}").hasAuthority("USER")
                        .requestMatchers("/titles/delete/{titleId}").hasAuthority("USER")
                        .requestMatchers("/titles/get/{titleId}").hasAuthority("USER")
                        .requestMatchers("/titles/get-all-of-project/{projectId}").hasAuthority("USER")

                        .requestMatchers("/members/add").hasAuthority("USER")
                        .requestMatchers("/members/update/{memberId}").hasAuthority("USER")
                        .requestMatchers("/members/delete/{userCode}/{projectId}").hasAuthority("USER")
                        .requestMatchers("/members/get/{memberId}").hasAuthority("USER")
                        .requestMatchers("/members/get-all-of-project/{projectId}").hasAuthority("USER")

                        .requestMatchers("/attendance/record-attendance/{userCode}/{projectId}").hasAuthority("USER")
                        .requestMatchers("/attendance/end-attendance/{userCode}/{projectId}").hasAuthority("USER")
                        .requestMatchers("/attendance/get-daily-attendances/{projectId}/{userCode}").hasAuthority("USER")
                        .requestMatchers("/attendance/get-daily-attendances/{projectId}").hasAuthority("USER")
                        .requestMatchers("/attendance/get-active-attendances/{projectId}").hasAuthority("USER")
                        .requestMatchers("/attendance/get-absences/{projectId}/{userCode}").hasAuthority("USER")
                        .requestMatchers("/attendance/get-absences/{projectId}").hasAuthority("USER")

                        .requestMatchers("/tasks/add").hasAuthority("USER")
                        .requestMatchers("/tasks/update/{taskId}").hasAuthority("USER")
                        .requestMatchers("/tasks/delete/{taskId}").hasAuthority("USER")
                        .requestMatchers("/tasks/get/{taskId}").hasAuthority("USER")
                        .requestMatchers("/tasks/finish/{taskId}").hasAuthority("USER")
                        .requestMatchers("/tasks/assign-to-member/{taskId}/{userCode}").hasAuthority("USER")
                        .requestMatchers("/tasks/get-all-of-member/{userCode}").hasAuthority("USER")
                        .requestMatchers("/tasks/get-all-of-project/{projectId}").hasAuthority("USER")
                        .requestMatchers("/tasks/get-all-of-member-in-project/{userCode}/{projectId}").hasAuthority("USER")
                );
        //http.formLogin(withDefaults());
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        CustomUsernamePasswordAuthenticationProvider authenticationProvider =
                new CustomUsernamePasswordAuthenticationProvider(userDetailsService, passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return  providerManager;
    }


}
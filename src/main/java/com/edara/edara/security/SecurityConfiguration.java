//package com.edara.edara.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.password.CompromisedPasswordChecker;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration {
//
//
//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrfConfig -> csrfConfig.disable())
//                .authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("/users/edit-profile/{userId}", "/users/get-by-id/{userId}").authenticated()
//                        .requestMatchers("/auth/login", "/users/register").permitAll());
//        http.formLogin(withDefaults());
//        http.httpBasic(withDefaults());
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
//
//    /**
//     * From Spring Security 6.3 version
//     *
//     * @return
//     */
//    @Bean
//    public CompromisedPasswordChecker compromisedPasswordChecker() {
//        return new HaveIBeenPwnedRestApiPasswordChecker();
//    }
//
//        @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//
//
//
//
//
////    @Bean
////    public CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration configuration = new CorsConfiguration();
////        //configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // React application's origin
////        //configuration.setAllowedOrigins(Arrays.asList("*")); // React application's origin
////        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
////        configuration.setAllowedHeaders(Arrays.asList("*"));
////        configuration.setAllowCredentials(true); // Important if you are sending cookies or using sessions
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/**", configuration);
////        return source;
////    }
////
////    @Bean
////    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
////        return configuration.getAuthenticationManager();
////    }
////
////    @Bean
////    public static PasswordEncoder passwordEncoder() {
////        return NoOpPasswordEncoder.getInstance();
////    }
////
////
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        http
////                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
////                .authorizeRequests(authorize -> authorize
////
////
////                        .requestMatchers(
////                                "/auth/login"
////                                , "/users/register"
////
////                        ).permitAll() // Permit all for these endpoints
////
////
////                        .requestMatchers("/users/edit-profile/{userId}").hasRole("USER")
////                        .requestMatchers("/users/get-by-id/{userId}").hasRole("USER")
////
////                        .anyRequest().authenticated()
////                )
////                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF if not used
////                .formLogin(Customizer.withDefaults())
////                .httpBasic(Customizer.withDefaults());
////        return http.build();
////    }
//
//
//
//}







package com.edara.edara.security;

import com.edara.edara.security.exceptionHandling.CustomAccessDeniedHandler;
import com.edara.edara.security.exceptionHandling.CustomBasicAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrfConfig -> csrfConfig.disable()) // Consider enabling CSRF protection
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/users/edit-profile/{userId}", "/users/get-by-id/{userId}").hasRole("USER")
                .requestMatchers("/auth/login", "/users/register").permitAll());
        http.formLogin(withDefaults());
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // React application's origin
        //configuration.setAllowedOrigins(Arrays.asList("*")); // React application's origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // Important if you are sending cookies or using sessions
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


}
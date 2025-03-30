package com.demo.sb.Configuration;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer {


    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {
        return new MappingJackson2HttpMessageConverter();
    }


    @Configuration
    public class JacksonConfig {
        @Bean
        public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
            return new Jackson2ObjectMapperBuilder()
                    .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Define an in-memory user matching spring.security.user.name and password
        User.UserBuilder users = User.builder().passwordEncoder(passwordEncoder()::encode);
        var user = users
                .username("admin")
                .password("1234") // Will be encoded by BCrypt
                .roles("USER")    // Adjust role as needed (e.g., "TEACHER", "STUDENT")
                .build();

        return new InMemoryUserDetailsManager(user);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/api/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                // Enable form login
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                // Enable HTTP Basic authentication
                .httpBasic(httpBasic -> httpBasic
                        .realmName("MyApp")
                )
                // Disable CSRF for simplicity (e.g., for stateless APIs)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
/*
@Configuration
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/api/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                .formLogin().and().httpBasic();
        return http.build();
    }

}
*/
//with security
/*
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*
@Configuration
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/api/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                // Enable form login (modern way)
                .formLogin(form -> form
                        .loginPage("/login") // Optional: specify a custom login page if needed
                        .permitAll()         // Allow everyone to access the login page
                )
                // Enable HTTP Basic authentication (modern way)
                .httpBasic(httpBasic -> httpBasic
                        .realmName("MyApp")  // Optional: specify a realm name
                )
                // CSRF protection (enabled by default, but you can customize or disable if needed)
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity (e.g., for stateless APIs)
                // Session management (optional, depending on your app's needs)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
*/
/* package com.demo.sb.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/files/**") // Allow CORS for the /files path
                .allowedOrigins("http://localhost:4200") // Allow requests from Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow these methods
                .allowedHeaders("*") // Allow any headers
                .allowCredentials(true); // Allow credentials if necessary
    }
}*/

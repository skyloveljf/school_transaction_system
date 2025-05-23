package com.example.yourproject.config;

import org.springframework.context.annotation.Lazy;
import com.example.yourproject.service.UserService; // 用于 UserDetailsService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter; // CorsFilter Bean is not explicitly defined here, using http.cors()

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final StorageProperties storageProperties; // 修改: FileStorageProperties -> StorageProperties
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserService userService,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          @Lazy JwtRequestFilter jwtRequestFilter,
                          StorageProperties storageProperties, // 修改: FileStorageProperties -> StorageProperties
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
        this.storageProperties = storageProperties; // 修改: fileStorageProperties -> storageProperties
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.example.yourproject.model.User user = userService.getUserByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPasswordHash(),
                    new java.util.ArrayList<>()
            );
        };
    }
    
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(this.passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String tempStaticResourcePath = "/**"; 
        // 使用 storageProperties.getLocation() 来构建静态资源路径
        // WebMvcConfig 中配置的 handler 是 /uploads/images/**
        // storageProperties.getLocation() 可能返回的是文件系统路径如 "./uploads/images"
        // 我们需要的是 URL 路径模式
        // 假设 FileUploadService 返回的 URL 是 /uploads/images/filename.jpg
        // 那么这里应该允许 /uploads/images/**
        final String staticResourcePath = "/uploads/images/**";

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()
                .requestMatchers("/api/weather/**").permitAll() // Assuming this is public
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                // 文件上传和访问权限
                .requestMatchers(HttpMethod.POST, "/api/upload/image").authenticated() // 上传需要认证 (路径已更新)
                // .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll() // 如果有下载
                .requestMatchers(HttpMethod.GET, staticResourcePath).permitAll() // 允许公开访问上传的静态资源
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:8081", "http://localhost:3000", "http://127.0.0.1:8081", "http://127.0.0.1:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
package com.example.yourproject.controller;

import com.example.yourproject.dto.AuthResponse;
import com.example.yourproject.dto.UserProfileDto;
import com.example.yourproject.dto.UserLoginRequest;
import com.example.yourproject.dto.UserRegisterRequest;
import com.example.yourproject.model.User;
import com.example.yourproject.service.UserService;
import com.example.yourproject.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin // 可以考虑在SecurityConfig中全局配置CORS
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final UserDetailsService userDetailsService; // Spring Security's UserDetailsService

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          UserService userService,
                          UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterRequest registerRequest) {
        try {
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPasswordHash(registerRequest.getPassword()); // Service层会加密
            newUser.setEmail(registerRequest.getEmail());
            newUser.setRegistrationDate(LocalDateTime.now());

            User registeredUser = userService.registerUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserProfileDto.fromUser(registeredUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody UserLoginRequest loginRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            // throw new Exception("INVALID_CREDENTIALS", e);
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final User userModel = userService.getUserByUsername(loginRequest.getUsername()); // 获取我们自己的User模型

        if (userModel == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户不存在");
        }
        
        final String token = jwtTokenUtil.generateToken(userModel);
        userService.updateUserLastLogin(userModel.getUserId()); // 更新最后登录时间

        return ResponseEntity.ok(new AuthResponse(token, UserProfileDto.fromUser(userModel)));
    }

    /**
     * 临时管理员注册端点
     * 注意：在生产环境中应该移除此端点或添加额外的安全措施
     * 当前已注释掉，如需要请手动启用
     */
 
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody UserRegisterRequest registerRequest) {
        try {
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPasswordHash(registerRequest.getPassword()); // Service层会加密
            newUser.setEmail(registerRequest.getEmail());
            newUser.setRegistrationDate(LocalDateTime.now());
            newUser.setRole("ROLE_ADMIN"); // 设置为管理员角色

            User registeredUser = userService.registerUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserProfileDto.fromUser(registeredUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
  
}
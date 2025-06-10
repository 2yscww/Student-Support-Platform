package team.work.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import team.work.platform.common.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/payment/notify").disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/register", "/api/user/login", "/api/user/send-code","/api/orders/list", "/api/payment/notify").permitAll()
                        // ! 管理员查看任务详情
                        .requestMatchers("/api/orders/detail").hasAnyRole("ADMIN","USER")
                        // .requestMatchers("/api/admin/reports/detail").hasRole("ADMIN")
                        // ! 管理员专用接口 - 只允许纯管理员角色访问
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // ? 用户接口 - 只允许纯用户角色访问
                        .requestMatchers("/api/user/**").hasRole("USER")
                        // ? 用户的订单接口 只允许用户访问
                        .requestMatchers("/api/orders/**").hasRole("USER")
                        // ? 用户的支付接口
                        .requestMatchers("/api/payment/pay").hasRole("USER")
                        // ? 用户的举报接口
                        .requestMatchers("/api/reports/**").hasRole("USER")
                        // ? 用户的评论接口
                        .requestMatchers("/api/reviews/**").hasRole("USER")
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080"); // 前端开发服务器地址
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

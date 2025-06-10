package team.work.platform.common;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import team.work.platform.mapper.UsersMapper;
import team.work.platform.model.Users;
import team.work.platform.model.enumValue.Status;
import team.work.platform.utils.JwtUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // 定义白名单，这些路径不受用户状态限制
    private static final List<String> allowedPaths = Arrays.asList(
        "/api/user/login",
        "/api/admin/login",
        "/api/user/register",
        "/api/user/profile",
        "/api/admin/profile"
        // 如果有登出接口，也应加入
        // "/api/auth/logout" 
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        String userEmail = null;
        String jwt = null;

        // ? 将用户的邮箱放入jwt
        
        // 检查Authorization头是否存在且以Bearer开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                userEmail = jwtUtil.getUserEmailFromToken(jwt);
            } catch (IllegalArgumentException e) {
                logger.error("无法获取JWT令牌");
            } catch (ExpiredJwtException e) {
                logger.error("JWT令牌已过期");
            } catch (MalformedJwtException e) {
                logger.error("无效的JWT令牌");
            }
        }
        
        // 如果成功获取用户邮箱且安全上下文中没有认证信息，则进行认证
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            Users user = usersMapper.selectByUserEmail(userEmail);
            
            if (user != null && jwtUtil.validateToken(jwt, userEmail)) {

                // 检查用户状态
                String requestPath = request.getRequestURI();
                boolean isPathAllowed = allowedPaths.stream().anyMatch(requestPath::startsWith);

                if (!isPathAllowed && (user.getStatus() == Status.FROZEN || user.getStatus() == Status.BANNED)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    String message = "你的账户处于监管状态";
                    response.getWriter().write(objectMapper.writeValueAsString(Response.Fail(null, message)));
                    return;
                }

                String role = jwtUtil.getUserRoleFromToken(jwt);
                Long userId = jwtUtil.getUserIdFromToken(jwt);

                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role));

                Map<String, Object> details = new HashMap<>();
                details.put("userId", userId);
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail, null, authorities);
                
                // 设置认证详情
                authToken.setDetails(details);
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    // 获取当前用户ID的静态方法
    public static Long getCurrentUserId() {
        try {
            Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
            if (details instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Long> detailsMap = (Map<String, Long>) details;
                return detailsMap.get("userId");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
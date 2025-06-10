package team.work.platform.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long userId;
    private String username;
    private String email;
    private Integer creditScore;
    private String status;
    private String role;
    private Date createdAt;

    // Getters and Setters

} 
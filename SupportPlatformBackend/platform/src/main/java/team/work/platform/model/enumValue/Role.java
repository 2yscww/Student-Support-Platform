package team.work.platform.model.enumValue;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN");
    
    
    // * 数据库字段
    // * role ENUM('USER', 'ADMIN') DEFAULT 'USER'

    private final String value;
    Role(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}

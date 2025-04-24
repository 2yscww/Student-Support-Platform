package team.work.platform.model.enumValue;

public enum Status {
    ACTIVE("ACTIVE"),
    FROZEN("FROZEN"),
    BANNED("BANNED");

    private final String value;

    // 构造方法
    Status(String value) {
        this.value = value;
    }

    // 获取枚举值
    public String getValue() {
        return value;
    }

  
}
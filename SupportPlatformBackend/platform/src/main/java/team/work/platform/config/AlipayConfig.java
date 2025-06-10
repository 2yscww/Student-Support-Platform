package team.work.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;
    private String gatewayUrl;
    private String signType;
    private String charset;

    private CertPath certPath;

    @Data
    public static class CertPath {
        private String appCert;
        private String alipayCert;
        private String alipayRootCert;
    }
} 
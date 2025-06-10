package team.work.platform.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;
 
public interface PaymentService {
    String createPayment(Integer orderId) throws AlipayApiException;
    boolean handlePaymentNotification(Map<String, String> params);
} 
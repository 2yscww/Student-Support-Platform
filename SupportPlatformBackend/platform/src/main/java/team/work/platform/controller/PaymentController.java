package team.work.platform.controller;

import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team.work.platform.common.Response;
import team.work.platform.dto.PayRequestDTO;
import team.work.platform.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public Response<Object> pay(@RequestBody PayRequestDTO payRequestDTO) {
        try {
            String payForm = paymentService.createPayment(payRequestDTO.getOrderId());
            return Response.Success(payForm, "请完成支付");
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return Response.Fail(null, "支付服务出错");
        } catch (RuntimeException e) {
            return Response.Fail(null, e.getMessage());
        }
    }

    @PostMapping("/notify")
    public String alipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        boolean success = paymentService.handlePaymentNotification(params);

        if (success) {
            return "success";
        } else {
            return "failure";
        }
    }
} 
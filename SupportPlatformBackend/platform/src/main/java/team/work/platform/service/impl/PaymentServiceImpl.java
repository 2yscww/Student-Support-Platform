package team.work.platform.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.internal.util.AlipaySignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import team.work.platform.config.AlipayConfig;
import team.work.platform.mapper.OrdersMapper;
import team.work.platform.mapper.TaskMapper;
import team.work.platform.model.Orders;
import team.work.platform.model.Tasks;
import team.work.platform.model.enumValue.OrderStatus;
import team.work.platform.model.enumValue.PaymentStatus;
import team.work.platform.service.PaymentService;

import java.io.IOException;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrdersMapper ordersMapper;
    
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public String createPayment(Integer orderId) throws AlipayApiException {
        Orders order = ordersMapper.selectOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("订单已支付");
        }

        Tasks task = taskMapper.selectById(order.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        try {
            CertAlipayRequest certRequest = new CertAlipayRequest();
            certRequest.setServerUrl(alipayConfig.getGatewayUrl());
            certRequest.setAppId(alipayConfig.getAppId());
            certRequest.setPrivateKey(alipayConfig.getAppPrivateKey());
            certRequest.setFormat("json");
            certRequest.setCharset(alipayConfig.getCharset());
            certRequest.setSignType(alipayConfig.getSignType());
            
            // Resolve classpath resources to absolute paths
            certRequest.setCertPath(new ClassPathResource(alipayConfig.getCertPath().getAppCert()).getURL().getPath());
            certRequest.setAlipayPublicCertPath(new ClassPathResource(alipayConfig.getCertPath().getAlipayCert()).getURL().getPath());
            certRequest.setRootCertPath(new ClassPathResource(alipayConfig.getCertPath().getAlipayRootCert()).getURL().getPath());
            
            AlipayClient alipayClient = new DefaultAlipayClient(certRequest);

            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setNotifyUrl(alipayConfig.getNotifyUrl());

            request.setBizContent("{" +
                "\"out_trade_no\":\"" + order.getOrderId() + "\"," +
                "\"total_amount\":\"" + task.getReward() + "\"," +
                "\"subject\":\"" + "支付任务: " + task.getTitle() + "\"" +
                "}");

            AlipayTradePrecreateResponse response = alipayClient.certificateExecute(request);
            if(response.isSuccess()){
                return response.getQrCode();
            } else {
                throw new AlipayApiException("Alipay precreate failed. " + response.getSubMsg());
            }
        } catch (IOException e) {
             e.printStackTrace();
             throw new AlipayApiException("无法加载证书文件，请检查路径配置。", e);
        }
    }

    @Override
    public boolean handlePaymentNotification(Map<String, String> params) {
        logger.info("支付宝异步通知抵达，参数: {}", params);
        try {
            String alipayCertPath = new ClassPathResource(alipayConfig.getCertPath().getAlipayCert()).getURL().getPath();
            boolean signVerified = AlipaySignature.rsaCertCheckV1(
                params,
                alipayCertPath,
                alipayConfig.getCharset(),
                alipayConfig.getSignType()
            );
            
            logger.info("支付宝验签结果: {}", signVerified);

            if (signVerified) {
                String outTradeNo = params.get("out_trade_no");
                String tradeStatus = params.get("trade_status");
                logger.info("订单号: {}, 交易状态: {}", outTradeNo, tradeStatus);

                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    long orderId = Long.parseLong(outTradeNo);
                    Orders order = ordersMapper.selectById(orderId);
                    if (order != null && order.getPaymentStatus() == PaymentStatus.UNPAID) {
                        logger.info("准备更新订单 {} 的支付状态为 PAID", orderId);
                        order.setPaymentStatus(PaymentStatus.PAID);
                        int updatedRows = ordersMapper.updateById(order);
                        if (updatedRows > 0) {
                            logger.info("订单 {} 支付状态更新成功", orderId);
                            return true;
                        } else {
                            logger.error("订单 {} 支付状态更新失败，数据库未更新", orderId);
                        }
                    } else if (order == null) {
                        logger.warn("根据订单号 {} 未找到对应订单", orderId);
                    } else {
                        logger.warn("订单 {} 的支付状态已经是 {}，无需重复处理", orderId, order.getPaymentStatus());
                    }
                }
            } else {
                logger.error("支付宝通知验签失败，请检查证书配置是否正确。");
            }
        } catch (AlipayApiException | IOException e) {
            logger.error("处理支付宝通知时发生异常", e);
        }
        return false;
    }
} 
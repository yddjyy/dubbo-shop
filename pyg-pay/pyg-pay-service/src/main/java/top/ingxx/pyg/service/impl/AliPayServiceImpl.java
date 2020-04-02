package top.ingxx.pyg.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.springframework.beans.factory.annotation.Value;
import top.ingxx.pay.service.AliPayService;

import java.util.HashMap;
import java.util.Map;
@Service
public class AliPayServiceImpl implements AliPayService {

    @Value("${app_id}")
    private String appid;

    @Value("${merchant_private_key}")
    private String merchantPrivateKey;

    @Value("${alipay_public_key}")
    private String alipayPublicKey;

   // @Value("${sign_type}")
    private String signType = "RSA2";

    @Value("${charset}")
    private String charset;

    @Value("${gatewayUrl}")
    private String gatewayUrl;
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1 封装参数
        Map map = new HashMap();
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,appid,merchantPrivateKey,"json",charset, alipayPublicKey,signType);
        AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(out_trade_no);
        model.setTotalAmount(total_fee);
        model.setSubject("百瑞达");
        //2 发送请求
        alipayRequest.setBizModel(model);
        try {
            AlipayTradePrecreateResponse result = alipayClient.execute(alipayRequest);
            System.out.println(result.getMsg()+","+result.getSubMsg());
            //  3 获取结果
            map.put("code_url",result.getQrCode());
            map.put("out_trade_no",out_trade_no);
            map.put("total_fee",total_fee);
            System.out.println("订单返回码"+result.getQrCode());
            return map;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        HashMap map = new HashMap();
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,appid,merchantPrivateKey,"json",charset, alipayPublicKey,signType);

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(out_trade_no);
        alipayRequest.setBizModel(model);
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);
             map.put("out_trade_no",response.getOutTradeNo());
             if(response.getTradeStatus()==null){
                 map.put("trade_status","TRADE_CREATE");
             }else{
                 map.put("trade_status",response.getTradeStatus());
             }
             map.put("total_amount",response.getTotalAmount());
             map.put("trade_no",response.getTradeNo());
            return map;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Map closePay(String out_trade_no) {

        HashMap map = new HashMap();
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,appid,merchantPrivateKey,"json",charset, alipayPublicKey,signType);

        //设置请求参数
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
        AlipayTradeCloseModel model = new AlipayTradeCloseModel();
        model.setOutTradeNo(out_trade_no);
        alipayRequest.setBizModel(model);
        try {
            AlipayTradeCloseResponse response = alipayClient.execute(alipayRequest);
            map.put("out_trade_no",response.getOutTradeNo());
            map.put("trade_no",response.getTradeNo());
            map.put("sub_msg",response.getSubMsg());
            return map;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return null;
        }

    }
}

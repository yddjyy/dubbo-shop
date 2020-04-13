package top.ingxx.pay.service;

import java.util.Map;

public interface AliPayService {
    /**
     * 生成二维码
     * @param out_trade_no 商户订单号
     * @param total_fee //金额
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    /**
     * 查询支付订单状态
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatus(String out_trade_no);


    /**
     * 关闭订单
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);


    public Map refundPay(String outTradeNo,String refund_amount);
}

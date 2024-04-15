package com.example.pcmall.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Order {
    private Integer id;
    private String oid;//订单编号
    private String uid;//用户编号
    private List<Goods> goodsList;//订单商品信息
    private Address address;//地址信息
    private BigDecimal price;//总金额
    private Integer status;//状态（0待付款、1待发货、2待收货、3交易成功、4交易取消、5退货中、6退货成功）
    private LocalDateTime createdTime;//创建时间
    private LocalDateTime payTime;//付款时间
    private LocalDateTime sendTime;//发货时间
    private LocalDateTime finishTime;//完成时间
}

package com.example.pcmall.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Cart {
    private Integer id;//购物车信息编号
    private String uid;//用户编号
    private Integer gid;//商品编号
    private Goods goods;
    private Integer count;//选购数量
    private LocalDateTime createdTime;//创建时间
    private Integer isSelect;//0为未选购，1为选购
}

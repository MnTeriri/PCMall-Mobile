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
public class Brand {
    private Integer id;//品牌id
    private String bname;//品牌名称
    private LocalDateTime createdTime;//创建时间
    private LocalDateTime updateTime;//修改时间
    private Long categoryCount;
    private String image;
    private Integer isDelete;//是否删除（0正常 1删除）
}

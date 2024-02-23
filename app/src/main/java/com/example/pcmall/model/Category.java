package com.example.pcmall.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class Category {
    private Integer id;//分类编号
    private String cname;//分类名称
    private LocalDateTime createdTime;//创建时间
    private LocalDateTime updateTime;//修改时间
    private Integer isDelete;//是否删除（0正常 1删除）
}

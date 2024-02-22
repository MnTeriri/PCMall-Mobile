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
public class User {
    private Integer id;
    private String uid;//用户号
    private String uname;//用户名
    private String password;//密码（MD5加密）
    private LocalDateTime createdTime;//创建时间
    private LocalDateTime loginTime;//最后一次登录时间
    private String image;
    private Integer isDelete;//是否删除（0正常 1删除）
}

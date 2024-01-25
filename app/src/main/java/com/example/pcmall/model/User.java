package com.example.pcmall.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private Integer id;
    private String uid;//用户号
    private String uname;//用户名
    private String password;//密码（MD5加密）
    private Integer isDelete;//0正常，1删除
    private LocalDateTime createdTime;//创建时间
    private LocalDateTime loginTime;//最后一次登录时间
    //private byte[] image;
    //private String imageString;//头像
}

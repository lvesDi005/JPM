package com.itcast.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String openid;
    private String name;
    private String phone;
    private String sex;
    private String idNumber;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
package com.itcast.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Employee {
    private Long id;
    private String username;
    private String name;
    private String password;
    private String phone;
    private String sex;
    private String idNumber;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}
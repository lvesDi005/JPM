package com.itcast.mapper;

import com.itcast.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee getByUsername(String username);
}
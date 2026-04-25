package com.itcast.mapper;

import com.itcast.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getById(Long id);

    @Select("SELECT * FROM user WHERE name = #{name}")
    User getByName(String name);
}
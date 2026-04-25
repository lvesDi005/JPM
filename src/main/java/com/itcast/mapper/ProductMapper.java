package com.itcast.mapper;

import com.itcast.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT id, name, category_id, price, image, description, status FROM product ORDER BY id")
    List<Product> listAll();
}

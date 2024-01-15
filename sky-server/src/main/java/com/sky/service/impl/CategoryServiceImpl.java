package com.sky.service.impl;


import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        //调用持久层，建议DTO转成实体
        Category category = new Category();

        //对象属性的拷贝(DTO中的数据不完全，需要自己再补充完整)
        BeanUtils.copyProperties(categoryDTO,category);

        //设置分类状态 1：启用  0：禁用
        category.setStatus(StatusConstant.ENABLE);

        //设置创建时间和修改时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());

        //设置当前创建人和修改人的id
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        /**
         * 调用持久层：插入分类的数据
         */
        categoryMapper.save(category);


    }
}

package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
     *
     * @param categoryDTO
     */
    @Override
    public void save(CategoryDTO categoryDTO) {
        //调用持久层，建议DTO转成实体
        Category category = new Category();

        //对象属性的拷贝(DTO中的数据不完全，需要自己再补充完整)
        BeanUtils.copyProperties(categoryDTO, category);

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

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        //select * from category limit 0,10
        //使用分页查询插件PageHelper来完成
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        //使用这个插件就必须返回Page
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        long total = page.getTotal();
        List<Category> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 根据id删除分类
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if (count > 0) {
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if (count > 0) {
            //当前分类下有套餐，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除分类数据
        categoryMapper.deleteById(id);
    }


    /**
     * 启用、禁用分类
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //update category set status ? where id = ?
        Category category = Category.builder()
                .status(status)
                .id(id)
                .build();
        categoryMapper.update(category);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        //调用持久层，建议dto转成实体
        Category category = new Category();
        //对象属性的拷贝
        BeanUtils.copyProperties(categoryDTO,category);
        //补全数据:修改时间、修改人(只补充修改的数据)
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);

    }
}

package com.test.hello.mapper;

import java.io.Serializable;

/**
 * DAO公共基类，由MybatisGenerator自动生成请勿修改
 * @param <Model> The Model Class 这里是泛型不是Model类
 * @param <PK> The Primary Key Class 如果是无主键，则可以用Model来跳过，如果是多主键则是Key类
 */
public interface MyBatisBaseDao<Model, PK extends Serializable> {

    /**
     * 根据id删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(PK id);

    /**
     * 新增
     * @param record
     * @return
     */
    int insert(Model record);

    /**
     * 按实际字段保存
     * @param record
     * @return
     */
    int insertSelective(Model record);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Model selectByPrimaryKey(PK id);

    /**
     * 按实际字段保存
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Model record);

    /**
     * 修改
     * @param record
     * @return
     */
    int updateByPrimaryKey(Model record);
}
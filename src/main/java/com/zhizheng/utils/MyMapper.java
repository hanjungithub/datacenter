package com.zhizheng.utils;

import tk.mybatis.mapper.common.Mapper;

/**
 * 继承自己的MyMapper
 * Created by DELL on 2017/12/13.
 */
public interface MyMapper<T> extends Mapper<T>{

    //TODO
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
}

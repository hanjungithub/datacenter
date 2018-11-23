package com.zhizheng.config.mybatis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;

@Configuration
public class MyBatisConfig {
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com/zhizheng/mapper/*.xml");

        Properties propertiesMapper = new Properties();
        propertiesMapper.setProperty("mappers", "tk.mybatis.mapper.common.Mapper");
        //propertiesMapper.setProperty("IDENTITY", "SELECT REPLACE(UUID(),''-'','''')");
        propertiesMapper.setProperty("ORDER", "BEFORE");
        mapperScannerConfigurer.setProperties(propertiesMapper);
        return mapperScannerConfigurer;
    }
}

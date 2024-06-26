package com.app.sns.aiproduct.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis的配置类
 *
 * @author 張
 * @version 0.0.1
 */
@Slf4j
@Configuration
@MapperScan("com.app.sns.aiproduct.mapper")
public class MybatisConfiguration {

}

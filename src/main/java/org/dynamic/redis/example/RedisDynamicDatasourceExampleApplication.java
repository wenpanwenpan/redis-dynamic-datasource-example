package org.dynamic.redis.example;

import org.enhance.redis.EnableRedisMultiDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author wenpan 2023/03/24 22:23
 */
@EnableRedisMultiDataSource
@SpringBootApplication
public class RedisDynamicDatasourceExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisDynamicDatasourceExampleApplication.class, args);
    }
}

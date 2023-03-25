package org.dynamic.redis.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.enhance.redis.annotation.EnableRedisMultiDataSource;
import org.enhance.redis.client.RedisMultiSourceClient;
import org.enhance.redis.helper.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.enhance.redis.infra.constant.DynamicRedisConstants.MultiSource.DEFAULT_SOURCE;

/**
 * 测试动态切换db
 * <p>
 * <li>
 * 注意：以集群模式连接Redis，或者 application.yml 文件中配置 spring.redis.dynamic-database=false时
 * IOC容器中注入的是静态 {@link RedisHelper} ，该 {@link RedisHelper} 不支持动态切换 Redis db.
 * 只能操作 spring.redis.database 配置指定的Redis db。
 * 当然，如上情况下 {@link RedisMultiSourceClient} 也不能动态切换Redis db。
 * 但是，只要是使用 {@link EnableRedisMultiDataSource} 开启了多数据源，那么你可以使用 {@link RedisMultiSourceClient} 来动态切换Redis 不同数据源
 * </li>
 * </p>
 *
 * @author Mr_wenpan@163.com 2021/09/06 22:46
 */
@Slf4j
@RestController("TestChangeDbController.v1")
@RequestMapping("/v1/test-change-db")
public class TestChangeDbController {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired(required = false)
    private RedisMultiSourceClient multisourceClient;

    //
    // 如下是测试单Redis数据源情况下，使用RedisHelper切换redis db使用案例
    // ---------------------------------------------------------------------------------------------------------

    /**
     * 前提：连接的Redis是非集群模式(集群模式Redis官方不支持动态切换db)，并且spring.redis.dynamic-database=true
     * 使用redisHelper提供的opsDbxxx方法操作默认Redis数据源切换db测试
     * 请求URL：http://localhost:20002/v1/test-change-db/test-01
     */
    @GetMapping("/test-01")
    public void test01() {
        // 操作1号db
        redisHelper.opsDbOne().opsForValue().set(getRandomValue(), getRandomValue());
        // 操作2号db
        redisHelper.opsDbTwo().opsForValue().set(getRandomValue(), getRandomValue());
        // 操作3号db
        redisHelper.opsDbThree().opsForValue().set(getRandomValue(), getRandomValue());
        // 操作4号db
        redisHelper.opsDbFour().opsForValue().set(getRandomValue(), getRandomValue());
    }

    /**
     * 前提：连接的Redis是非集群模式(集群模式Redis官方不支持动态切换db)，并且spring.redis.dynamic-database=true
     * 使用redisHelper手动指定默认Redis数据源db，切换db测试
     * 请求URL：http://localhost:20002/v1/test-change-db/test-02
     */
    @GetMapping("/test-02")
    public void test012() {
        try {
            redisHelper.setCurrentDatabase(1);
            redisHelper.lstRightPop("queue");
        } finally {
            // 必须手动清除当前线程里指定的db，防止线程重用影响，所以这种方式不是很友好，建议使用  redisHelper.opsDbxxx 方式
            redisHelper.clearCurrentDatabase();
        }
    }

    //
    // 如下是测试多Redis数据源情况下，使用 multisourceClient 任意操作某个数据源并且切换redis db使用案例
    // ---------------------------------------------------------------------------------------------------------

    /**
     * 前提：连接的Redis是非集群模式(集群模式Redis官方不支持动态切换db)，并且spring.redis.dynamic-database=true
     * 使用multisourceClient来操作不同数据，并且切换随意切换db测试
     * 请求URL：http://localhost:20002/v1/test-change-db/test-03
     */
    @GetMapping("/test-03")
    public void test03() {
        // 操作source1数据源1号db
        multisourceClient.opsDbOne("source1").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source1数据源2号db
        multisourceClient.opsDbTwo("source1").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source1数据源3号db
        multisourceClient.opsDbThree("source1").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source1数据源4号db
        multisourceClient.opsDbFour("source1").opsForValue().set(getRandomValue(), getRandomValue());

        // 操作source2数据源1号db
        multisourceClient.opsDbOne("source2").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source2数据源2号db
        multisourceClient.opsDbTwo("source2").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source2数据源3号db
        multisourceClient.opsDbThree("source2").opsForValue().set(getRandomValue(), getRandomValue());
        // 操作source2数据源4号db
        multisourceClient.opsDbFour("source2").opsForValue().set(getRandomValue(), getRandomValue());
    }

    /**
     * 前提：连接的Redis是非集群模式(集群模式Redis官方不支持动态切换db)，并且spring.redis.dynamic-database=true
     * 使用 multisourceClient 操作不存在的Redis数据源测试
     * 请求URL：http://localhost:20002/v1/test-change-db/test-04
     */
    @GetMapping("/test-04")
    public void test04() {
        // 错误测试，操作不存在的数据源
        multisourceClient.opsDbFour("source3").opsForValue().set(getRandomValue(), getRandomValue());
    }

    /**
     * 前提：连接的Redis是非集群模式(集群模式Redis官方不支持动态切换db)，并且spring.redis.dynamic-database=true
     * 使用 multisourceClient 操作0~15号库以外的db(需要redis上开启多个db)
     * 请求URL：http://localhost:20002/v1/test-change-db/test-05
     */
    @GetMapping("/test-05")
    public void test05() {
        // 这里操作 source2的30号db
        multisourceClient.opsOtherDb("source2", 30).opsForValue().set(getRandomValue(), getRandomValue());
    }

    /**
     * 前提：连接的Redis是非集群模式(集群模式Redis官方不支持动态切换db)，并且spring.redis.dynamic-database=true
     * 使用 multisourceClient 操作默认数据源的指定db
     * 请求URL：http://localhost:20002/v1/test-change-db/test-06
     */
    @GetMapping("/test-6")
    public void test6() {
        // 操作默认的数据源的1号db
        multisourceClient.opsDbOne(DEFAULT_SOURCE).opsForValue().set(getRandomValue(), getRandomValue());
        // 操作默认的数据源的2号db
        multisourceClient.opsDbTwo(DEFAULT_SOURCE).opsForValue().set(getRandomValue(), getRandomValue());
        // 操作默认的数据源的3号db
        multisourceClient.opsDbThree(DEFAULT_SOURCE).opsForValue().set(getRandomValue(), getRandomValue());
    }

    private String getRandomValue() {
        return UUID.randomUUID().toString();
    }

}
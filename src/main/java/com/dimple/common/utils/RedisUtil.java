package com.dimple.common.utils;

import com.dimple.framework.redis.RedisCacheService;
import redis.clients.jedis.Jedis;


public class RedisUtil {
    Jedis jedis = new Jedis("localhost");

    public RedisUtil() {
        //连接本地的 Redis 服务
        // 如果 Redis 服务设置了密码，需要下面这行，没有就不需要
//        jedis.auth("1234");
        System.out.println("Redis连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: " + jedis.ping());
    }

    public void setRedisCache(String str, String title) {
        jedis.set(str, title);
    }

    public String getRedisCache(String str) {
        return jedis.get(str);
    }

    public void deleteRedisCache(String str) {
        jedis.del(str);
    }
}

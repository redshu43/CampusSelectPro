package com.cy.newpt.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 1. 写入缓存
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    // 2. 写入缓存并设置过期时间（单位：秒）
    public void set(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    // 3. 读取缓存
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // 4. 删除缓存
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    // 5. 是否存在
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 递减操作，扣库存
     * @param key
     * @return 返回递减后的值
     */
    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    /**
     * 回滚库存/如果回sql对账的时候发现学生重复选课了，必须把刚才减掉的名额加回去
     * @param key
     * @return
     */
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 向set中添加数据
     * @param key
     * @param values 可以一次添加多个
     * @return 成功添加的个数
     */
    public Long sAdd(String key, String... values) {
        try {
            return stringRedisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 查询 Set 中是否存在某元素
     * @param key
     * @param value
     * @return true存在, false不存在
     */
    public Boolean sIsMember(String key, String value) {
        try{
            return stringRedisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean expire(String key, long time) {
        try{
            if (time > 0) {
                stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从 Key 中移除键值对
     * @param key
     * @param values
     * @return 移除的个数
     */
    public Long sRemove(String key, Object... values) {
        try{
            return stringRedisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 批量删除 key (支持模糊删除)
     * @param pattern 例如 "course:*"
     */
    public void deleteByPattern(String pattern) {
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }


}

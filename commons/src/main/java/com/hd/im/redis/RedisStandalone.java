package com.hd.im.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;

/**
 * @author lyon.cao
 */
public enum RedisStandalone {

    REDIS;

    Logger logger = LoggerFactory.getLogger(RedisStandalone.class);

    JedisPool jedisPool = null;

    public JedisPool getJedisPool() {
        return jedisPool != null && !jedisPool.isClosed() ? jedisPool : null;
    }

    public void init(RedisConfig config) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(config.isTestOnBorrow());
        poolConfig.setTestOnReturn(config.isTestOnReturn());
        poolConfig.setMaxTotal(config.getMaxTotal());
        poolConfig.setMaxIdle(config.getMaxIdle());
        poolConfig.setMinIdle(config.getMinIdle());
        poolConfig.setMaxWaitMillis(config.getMaxWait());

        String host = config.getHost();
        Integer port = config.getPort();
        String password = config.getPassword();

        if (host == null || host.length() == 0) {
            logger.error("the redis host can not be null.");
            throw new RedisConfigException("the redis host can not be null.");
        }
        if (port <= 0) {
            logger.error("the redis port set fail.");
            throw new RedisConfigException("the redis port set fail.");
        }
        if (StrUtil.isNotBlank(password)) {
            jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            jedisPool = new JedisPool(poolConfig, host, port, 2000);
        }
        /* test connect */
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis.incrBy("redis_init_test", 1) >= 1) {
                logger.info("redis init success.");
            } else {
                System.exit(1);
            }
        } catch (Exception e) {
            logger.error("the redis connect pool init faild!", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    private Jedis getJedis() {
        if (jedisPool != null) {
            return jedisPool.getResource();
        }
        logger.error("can not be get jedis from connect pool.");
        return null;
    }

    /**
     * @param key
     * @param field
     * @return
     * @title: exist
     * @description: 判断子键是否存在
     * @author: Lyon
     * @return: boolean
     */
    public boolean exist(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public String scriptLoad(String script) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.scriptLoad(script);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @param key
     * @param field
     * @return
     * @title: hexists
     * @description: 判断子键是否存在
     * @author: Lyon
     * @return: boolean
     */
    public boolean hexists(String key, final String field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hexists(key, field);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * @param key
     * @param field
     * @title: hexists byte[]
     * @description: 判断子键是否存在
     * @return: boolean
     */
    public boolean hexists(byte[] key, final byte[] field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hexists(key, field);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * @param key
     * @param field
     * @return
     * @title: exist
     * @description: 判断子键是否存在
     * @author: Lyon
     * @return: boolean
     */
    public Long expire(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1L;
    }

    /**
     * @param key
     * @param field
     * @return
     */
    public String set(String key, String val) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.set(key, val);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @param key
     * @param val
     * @param seconds
     * @return String
     * @Description set key expire
     * @Version 1.0
     * @Author matt
     * @Date 2019/12/11 19:27
     */
    public String setExpire(String key, String val, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.setex(key, seconds, val);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @param key
     * @param field
     * @return
     */
    public byte[] hget(byte[] key, byte[] field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hgetAll(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @param key
     * @return
     */
    public Map<byte[], byte[]> hgetAll(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hgetAll(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * hset
     *
     * @param key
     * @param data
     */
    public void hmset(String key, Map<String, String> data) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hmset(key, data);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * hset
     *
     * @param key
     * @param data
     */
    public void hset(String key, String field, String val) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hset(key, field, val);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * hset byte
     *
     * @param key
     * @param data
     */
    public void hset(byte[] key, byte[] field, byte[] val) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hset(key, field, val);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * hdel
     *
     * @param key
     * @param data
     */
    public void hdel(String key, String... fields) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hdel(key, fields);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long hlen(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hlen(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * hvals
     *
     * @param key
     */
    public Set<String> hkeys(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hkeys(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * hvals byte[]
     *
     * @param key
     */
    public Set<byte[]> hkeys(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hkeys(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * hvals
     *
     * @param key
     */
    public List<String> hvals(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hvals(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * hvals byte[]
     *
     * @param key
     */
    public List<byte[]> hvals(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hvals(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Long lpush(String key, String fields) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpush(key, fields);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1L;
    }

    /**
     * brpop
     *
     * @param key
     */
    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.brpop(timeout, key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * blpop
     *
     * @param key
     */
    public List<String> blpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.blpop(timeout, key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long llen(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.llen(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * incrBy
     *
     * @param key
     * @param increment
     */
    public Long incrBy(String key, long increment) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incrBy(key, increment);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * hincrBy
     *
     * @param key
     * @param field
     * @param increment
     */
    public Long hincrBy(String key, String field, long increment) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hincrBy(key, field, increment);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * get string
     *
     * @param key
     */
    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 发布消息到指定的频道
     *
     * @param channel
     * @param message
     */
    public void publish(final String channel, final String message) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.publish(channel, message);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 订阅给定的一个或多个频道的信息
     *
     * @param jedisPubSub
     * @param channels
     */
    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.subscribe(jedisPubSub, channels);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * @param key
     * @param field
     * @return Long
     * @Description Set添加元素
     * @Version 1.0
     * @Author matt
     * @Date 2019/11/21 19:59
     */
    public Long sadd(String key, String... field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key, field);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    /**
     * @param key
     * @param field
     * @return Long
     * @Description Set添加元素 byte
     * @Version 1.0
     * @Author matt
     * @Date 2020/2/19 13:52
     */
    public Long sadd(byte[] key, byte[]... field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key, field);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    /**
     * @param key
     * @return Set
     * @Description Set判断属于成员
     * @Version 1.0
     * @Author matt
     * @Date 2019/11/22 14:40
     */
    public boolean sismembers(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sismember(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * @param key
     * @return Set
     * @Description Set查询所有成员
     * @Version 1.0
     * @Author matt
     * @Date 2019/11/22 14:40
     */
    public Set<String> smembers(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.smembers(key);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * @param key
     * @return Set
     * @Description Set查询所有成员
     * @Version 1.0
     * @Author matt
     * @Date 2019/11/22 14:40
     */
    public Set<byte[]> smembers(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.smembers(key);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * @param key
     * @param field
     * @return null
     * @Description Set移除元素
     * @Version 1.0
     * @Author matt
     * @Date 2019/12/2 16:12
     */
    public Long srem(String key, String... field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.srem(key, field);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    /**
     * @param key
     * @return null
     * @Description 移除key
     * @Version 1.0
     * @Author matt
     * @Date 2019/12/19 11:44
     */
    public void del(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * @param null
     * @return null
     * @Description 查询符合表达式的key集合
     * @Version 1.0
     * @Author matt
     * @Date 2019/12/17 18:15
     */
    public Set<String> keys(String pattern) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.keys(pattern);
        } catch (Exception e) {
            logger.error("can't find any key");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @Description 查询指定区间内的成员
     * @Version 1.0
     * @Author matt
     * @Date 2020/2/21 9:24
     */
    public Set<String> zrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("can't find any key");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     * @title: zrangeWithScore
     * @description: zrange with score
     * @author: Lyon.Cao
     * @return: Set<Tuple>
     */
    public Set<Tuple> zrangeWithScore(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zrangeWithScores(key, start, end);
        } catch (Exception e) {
            logger.error("zrange with score faild.", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}

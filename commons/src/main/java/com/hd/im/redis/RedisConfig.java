package com.hd.im.redis;

/**
 * @ClassName: RedisConfig
 * @Description: REDIS 配置
 * @Author: Lyon.Cao
 * @Date: 2019年8月23日 下午2:12:48
 * @Version: v1.0
 */
public enum RedisConfig {

    CONFIGINSTANCE;

    /**
     * 主机 ip
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port = 6379;

    /**
     * redis 密码
     */
    private String password;

    /**
     * 可用连接实例的最大数目，默认值8； 如果赋值为-1，则表示不限制；如果pool已经分配了maxTotal个jedis实例，
     * 则此时pool的状态为exhausted(耗尽)。
     */
    private Integer maxTotal = 8;

    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值8
     */
    private Integer maxIdle = 8;

    /**
     * 控制一个pool最少有多少个状态为idle(空闲的)的jedis实例
     */
        private Integer minIdle = 0;

    /**
     * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。 如果超过等待时间，则直接抛出JedisConnectionException；
     */
    private Integer maxWait = -1;

    /**
     * 在borrow一个jedis实例时，是否提前进行validate操作； 如果为true，则得到的jedis实例均是可用的
     */
    private boolean testOnBorrow = false;

    /**
     * 当调用return Object方法时，是否进行有效性检查
     */
    private boolean testOnReturn = false;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        if (host == null || host.length() == 0) {
            return;
        }
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(String port) {
        if (isNumber(port)) {
            this.port = Integer.parseInt(port);
        }
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setMaxTotal(String maxTotal) {
        if (isNumber(maxTotal)) {
            this.maxTotal = Integer.parseInt(maxTotal);
        }
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMaxIdle(String maxIdle) {
        if (isNumber(maxIdle)) {
            this.maxIdle = Integer.parseInt(maxIdle);
        }

    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public void setMinIdle(String minIdle) {
        if (isNumber(minIdle)) {
            this.minIdle = Integer.parseInt(minIdle);
        }
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public void setMaxWait(String maxWait) {
        if (isNumber(maxWait)) {
            this.maxWait = Integer.parseInt(maxWait);
        }
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    private boolean isNumber(String val) {
        if (val == null || val.length() == 0 || !val.matches("\\d+")) {
            return false;
        }
        return true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
package com.atguigu.test;

import redis.clients.jedis.Jedis;

import java.util.Set;

public class Test {
    public static void main(String[] args) {
//        连接redis需要一个连接对象jedis
        Jedis jedis = new Jedis("192.168.88.128",6379);
        String ping = jedis.ping();
        System.out.println(ping);
        jedis.set("javaKey","java");
        String javaKey = jedis.get("javaKey");
        System.out.println(javaKey);
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
//            System.out.println(key);
            if (jedis.type(key).equals("String")){
                String s = jedis.get(key);
                System.out.println("字符串"+s);
            }
            if (jedis.type(key).equals("set")){
                jedis.smembers(key);
            }
        }
    }
}

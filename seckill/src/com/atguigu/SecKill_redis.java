package com.atguigu;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;


public class SecKill_redis {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SecKill_redis.class);

    public static void main(String[] args) {

        Jedis jedis = new Jedis("192.168.88.128", 6379);

        for (int i = 100; i > 0; i--) {
            jedis.rpush("sk:0101:qt", String.valueOf(i));
        }

        jedis.close();
    }

    public static boolean doSecKill(String uid, String prodid) throws IOException {
      Jedis jedis=new Jedis("192.168.88.128",6379);
      //库存的key
        String qtkey="sk:"+prodid+":qt";
        //用户的key
        String userKey = "sk:"+prodid+":usr";

        jedis.watch(qtkey);//监视库存1.0

        if (jedis.sismember(userKey,uid)){//判断set集合中的元素是否已经存在
            System.out.println("不能重复秒杀");
            jedis.close();
            return false;
        }

        String qt = jedis.get(qtkey);
        if (qt == null){//库没有初始化
            System.out.println("莫着急，活动还没有开始");
            jedis.close();
            return false;
        }
        int count = Integer.parseInt(qt);
        if (count <= 0){
            System.out.println("活动结束，手速有待提升");
            jedis.close();
            return false;
        }

        Transaction multi = jedis.multi();//开启事务
        //减库存
        multi.decr(qtkey);
        //加入的信息
        multi.sadd(userKey,uid);
        List<Object> exec = multi.exec();
        if (exec == null||exec.size()==0){
            System.out.println("抢购失败");
            jedis.close();
            return false;
        }
        System.out.println("秒杀成功");

        return true;
    }

}

















package com.ljs.miaosha.redis;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TestRedis {
	public static void main(String[] args) {
		Jedis jedis=null;
		try {
        	jedis=new Jedis("localhost",6379);
        	jedis.auth("123456");
            jedis.flushDB();
            testList(jedis);
        }finally {
        	if(jedis!=null) {
        		jedis.flushDB();
        		jedis.close();
        	}
        }
		//testString(jedis);
		
	}
    public static void testString(Jedis jedis){
        //设置键  获取键   get   set
        jedis.set("steve","stevetao");
        System.out.println("设置后值："+jedis.get("steve"));
        //追加键       append
        jedis.append("steve"," Is Good Man");
        System.out.println("追加后值："+jedis.get("steve"));
        //删除操作      del
        jedis.del("steve");
        System.out.println("删除后值："+jedis.get("steve"));
        //不存在就保存，   setnx   msetnx
        jedis.setnx("steve","stevetao");
        System.out.println("设置后值："+jedis.get("steve"));
        System.out.println("再次设置后值："+jedis.setnx("steve","stevetao"));
        //截取字符串      substr
        System.out.println("截取后值："+jedis.substr("steve",0,4));
        //设置多个键值对       mset    mget
        jedis.mset(new String[]{"zhangsan","123","lisi","1234"});
        System.out.println("多次设置后值："+jedis.mget("zhangsan","lisi"));
        //递增递减          incr  decr      incrby  decrby
        jedis.incr("zhangsan");
        jedis.decr("lisi");
        System.out.println("递增递减后值："+jedis.mget("zhangsan","lisi"));
        jedis.incrBy("zhangsan",6);
        jedis.decrBy("lisi",3);
        System.out.println("递增递减后值："+jedis.mget("zhangsan","lisi"));
    }
 
    public static void testList(Jedis jedis){
    	
        //尾添加 rpush          头添加  lpush
        jedis.lpush("books","java","C++","Ruby","Scala","python");
        jedis.rpush("language","java","C++","Ruby","Scala","python");
        //  -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
        System.out.println("头添加后books值:"+jedis.lrange("books",0,-1));
        System.out.println("尾添加后language值:"+jedis.lrange("language",0,-1));
        //尾部删除 rpop   头部删除 lpop
        System.out.println("删除的值为："+jedis.lpop("books"));
        System.out.println("删除的值为："+jedis.rpop("language"));
        System.out.println("头部删除后books值:"+jedis.lrange("books",0,-1));
        System.out.println("尾部删除后language值:"+jedis.lrange("language",0,-1));
        //尾部删除并头添加 rpoplpush
        jedis.rpoplpush("language","books");
        System.out.println("尾部删除并头添加后books值:"+jedis.lrange("books",0,-1));
        System.out.println("尾部删除并头添加后language值:"+jedis.lrange("language",0,-1));
        //区别:  只能给存在的list做添加，不能项lpush那样能新增list
        jedis.lpushx("books","php");
        jedis.lpushx("book","php");
        System.out.println("头添加后books值:"+jedis.lrange("books",0,-1));
        System.out.println("头添加后book值:"+jedis.lrange("book",0,-1));
        //获取集合长度 llen  指定索引的值 lindex   保留截取的值 ltrim
        System.out.println("books集合长度:"+jedis.llen("books"));
        System.out.println("books集合第二个数值:"+jedis.lindex("books",1));
        jedis.ltrim("books",0,2);
        System.out.println("截取后books值:"+jedis.lrange("books",0,-1));
    }
    
    public void testHash(Jedis jedis){
        //适合字段：设值hset  取值hget（如果value是json字符串，类似保存对象）
        jedis.hset("student","name","zhangsan");
        System.out.println("student中name的值为："+jedis.hget("student","name"));
        //适合对象：设值hmset      取值hmget
        Map<String,String> map = new HashMap<String,String>();
        map.put("name", "lisi");
        map.put("age", "36");
        jedis.hmset("teacher",map);
        System.out.println("teacher中name、age的值为："+jedis.hmget("teacher","name","age"));
        //teacher是否存在键age  hexists
        if(jedis.hexists("teacher","age")){
            //给指定值增加4   hincrBy
            jedis.hincrBy("teacher","age",4);
            System.out.println("teacher中name、age的值为："+jedis.hmget("teacher","name","age"));
        }
        //返回key的个数hlen      返回值hvals    返回键hkeys  键值对hgetAll
        jedis.hset("student","age","13");
        jedis.hset("student","qq","2246920330");
        jedis.hset("student","address","beijing");
        System.out.println("student中键的个数为："+jedis.hlen("student"));
        System.out.println("student中所有的键为："+jedis.hkeys("student"));
        System.out.println("student中所有的值为："+jedis.hvals("student"));
        System.out.println("student中所有的键值对为："+jedis.hgetAll("student"));
        //删除 hdel
        jedis.hdel("student",new String[]{"address","qq","age"});
        System.out.println("删除后，student中所有的键值对为："+jedis.hgetAll("student"));
    }
 
    public void testSet(Jedis jedis){
    	
        //set中添加值 sadd      取值 smembers
        jedis.sadd("student","Jan","John","Steve","jack","lili","peter","Anna");
        jedis.sadd("girls","Jan","lili","Alice","Jeanne","Anna");
        System.out.println("排名不分先后："+jedis.smembers("student"));
        //set个数 scard      是否存在某个值 sismember
        System.out.println("set集合的个数："+jedis.scard("student"));
        System.out.println("student是否存在steve："+jedis.sismember("student","Steve"));
        System.out.println("student是否存在stevetao："+jedis.sismember("student","Stevetao"));
        //System.out.println(jedis.sscan("student","0").getResult());
        //删除指定的值 srem     随机删除并返回 spop
        System.out.println("删除指定的值Steve："+jedis.srem("student","Steve"));
        System.out.println("删除的值为："+jedis.spop("student"));
        System.out.println("再次排名不分先后："+jedis.smembers("student"));
        //集合操作
        System.out.println("两个set的交集："+jedis.sinter("student","girls"));
        System.out.println("两个set的并集："+jedis.sunion("student","girls"));
        System.out.println("student对girls的差集："+jedis.sdiff("student","girls"));
        System.out.println("girls对student的差集："+jedis.sdiff("girls","student"));
        //集合操作并保存
        jedis.sinterstore("jiaoji","student","girls");
        jedis.sunionstore("bingji","student","girls");
        jedis.sdiffstore("chaji","student","girls");
 
        System.out.println("交集："+jedis.smembers("jiaoji"));
        System.out.println("并集："+jedis.smembers("bingji"));
        System.out.println("student对girls的差集："+jedis.smembers("chaji"));
    }
 
    public void testZset(Jedis jedis){
        jedis.zadd("math",75,"Jim");
        jedis.zadd("math",86,"Lina");
        jedis.zadd("math",52,"Dive");
        jedis.zadd("math",91,"Bobber");
        System.out.println("有序集合的成员数:"+jedis.zcard("math"));
        System.out.println("有序集合的成员:"+jedis.zrevrangeByScore("math",100,0));
        //返回set<Tuple>
        System.out.println("有序集合的成员:"+jedis.zrangeWithScores("math",0,100));
    }

}

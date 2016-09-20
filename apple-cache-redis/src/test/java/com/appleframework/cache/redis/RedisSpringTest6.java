package com.appleframework.cache.redis;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/apple-cache-redis-manager2.xml" })
public class RedisSpringTest6 {

	@Resource
	private JedisPool jedisPool;

	@SuppressWarnings("deprecation")
	@Test
	public void testAddOpinion1() {

		Jedis jedis = jedisPool.getResource();
		try {
			jedis.sadd("tom:friend:list", "123"); // tom�ĺ����б�
			jedis.sadd("tom:friend:list", "456");
			jedis.sadd("tom:friend:list", "789");
			jedis.sadd("tom:friend:list", "101");

			jedis.set("uid:sort:123", "1000"); // ���Ѷ�Ӧ�ĳɼ�
			jedis.set("uid:sort:456", "6000");
			jedis.set("uid:sort:789", "100");
			jedis.set("uid:sort:101", "5999");

			jedis.set("uid:123", "{'uid':123,'name':'lucy'}"); // ���ѵ���ϸ��Ϣ
			jedis.set("uid:456", "{'uid':456,'name':'jack'}");
			jedis.set("uid:789", "{'uid':789,'name':'marry'}");
			jedis.set("uid:101", "{'uid':101,'name':'icej'}");

			SortingParams sortingParameters = new SortingParams();

			sortingParameters.desc();
			sortingParameters.limit(0, 2);
			sortingParameters.get("uid:*");
			sortingParameters.get("uid:sort:*");
			sortingParameters.by("uid:sort:*");
			// ��Ӧ��redis ������./redis-cli sort tom:friend:list by uid:sort:* get uid:* get uid:sort:*
			List<String> result = jedis.sort("tom:friend:list", sortingParameters);
			for (String item : result) {
				System.out.println("item..." + item);
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			// ҵ�������ɣ������ӷ��ظ����ӳ�
			if (null != jedis) {
				jedisPool.returnResource(jedis);
			}
		}
	}

}

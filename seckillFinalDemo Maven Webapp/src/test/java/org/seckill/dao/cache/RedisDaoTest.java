package org.seckill.dao.cache;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.SecKill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.runner.BaseTestRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit Spring配置文件
@ContextConfiguration({"classpath:spring/springDao-config.xml"})
public class RedisDaoTest{
	
	private long id=1001;
	
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private RedisDao redisDao;

	/**
	 * 缓存redisDao一开始是空的，要通过seckillDao先去数据库取seckill对象，再存到缓存中
	 */
	@Test
	public void testSeckill() throws Exception{
		SecKill secKill=redisDao.getSeckill(id);
		if(secKill==null){
		//一开始就去缓存中找，是空的，就要再去数据库找
			secKill=seckillDao.queryById(id);
			if(secKill!=null){
				//从数据库中取出，把这个对象，存到redis缓存中
				String result=redisDao.putSeckill(secKill);
				System.out.println(result);
				//再去缓存中取
				secKill=redisDao.getSeckill(id);
				System.out.println(secKill);
			}
		}
	}

	
}

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
//����Junit Spring�����ļ�
@ContextConfiguration({"classpath:spring/springDao-config.xml"})
public class RedisDaoTest{
	
	private long id=1001;
	
	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private RedisDao redisDao;

	/**
	 * ����redisDaoһ��ʼ�ǿյģ�Ҫͨ��seckillDao��ȥ���ݿ�ȡseckill�����ٴ浽������
	 */
	@Test
	public void testSeckill() throws Exception{
		SecKill secKill=redisDao.getSeckill(id);
		if(secKill==null){
		//һ��ʼ��ȥ�������ң��ǿյģ���Ҫ��ȥ���ݿ���
			secKill=seckillDao.queryById(id);
			if(secKill!=null){
				//�����ݿ���ȡ������������󣬴浽redis������
				String result=redisDao.putSeckill(secKill);
				System.out.println(result);
				//��ȥ������ȡ
				secKill=redisDao.getSeckill(id);
				System.out.println(secKill);
			}
		}
	}

	
}

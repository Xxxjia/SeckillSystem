package org.seckill.dao.cache;

import org.seckill.entity.SecKill;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import ch.qos.logback.classic.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * ��Redis���д�������ݴ����
 * �д�redis��ȡ���ݵķ�����Ҳ����redis�������ݵķ���
 * @author xj
 */
public class RedisDao {
	private final Logger logger=(Logger) LoggerFactory.getLogger(this.getClass());
	private final JedisPool jedisPool;
	
	public RedisDao(String ip,int port){
		jedisPool=new JedisPool(ip,port);
	}
	//�õ�����ֽ��룬ȡһ�����ģʽ������ģʽ������Ӧ��ֵ
	private RuntimeSchema<SecKill> schema=RuntimeSchema.createFrom(SecKill.class);
	
	public SecKill getSeckill(long seckillId){
	   //redis�����߼�
		try{
			Jedis jedis=jedisPool.getResource();
			try{
				String key="seckill:"+seckillId;
				//��û��ʵ���ڲ����л�����
				//get->byte[] ->�����л� ->Object(SecKill)
				//�����Զ������л�
				//protostuff:������pojo
			   byte[] bytes=jedis.get(key.getBytes());
			   //�������ػ�ȡ��
			   if(bytes!=null){
				   //��һ��Seckill�Ŀն���,����schema�����˿ն�����
				   //��ԭ��ѹ����С�ҿ�                                                                                                                                                                                 
				   SecKill secKill=schema.newMessage();
				   ProtostuffIOUtil.mergeFrom(bytes, secKill, schema);
			       return secKill;
			   }
				
				
			}finally {
				jedis.close();
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
	public String putSeckill(SecKill secKill){
		//set Object(Seckill)->���л� -> byte[]
		try{
			Jedis jedis=jedisPool.getResource();
			try{
				String key="seckill:"+secKill.getSeckillId();
				//����ת��Ϊ�ַ����飬LinedBuffer�ǻ�����,��Ĭ�ϴ�С�ͺ�
				byte[] bytes=ProtostuffIOUtil.toByteArray(secKill,schema, 
					LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
		        //��ʱ����
				int timeout=60*60;//����һСʱ
				//���󷵻ش�����Ϣ����ȷ����OK
				String result=jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			
			}finally {
				jedis.close();
			}
		}catch (Exception e) {
		
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
}

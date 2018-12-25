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
 * 与Redis进行传输的数据传输层
 * 有从redis获取数据的方法，也有往redis存入数据的方法
 * @author xj
 */
public class RedisDao {
	private final Logger logger=(Logger) LoggerFactory.getLogger(this.getClass());
	private final JedisPool jedisPool;
	
	public RedisDao(String ip,int port){
		jedisPool=new JedisPool(ip,port);
	}
	//拿到类的字节码，取一个类的模式，根据模式赋予相应的值
	private RuntimeSchema<SecKill> schema=RuntimeSchema.createFrom(SecKill.class);
	
	public SecKill getSeckill(long seckillId){
	   //redis操作逻辑
		try{
			Jedis jedis=jedisPool.getResource();
			try{
				String key="seckill:"+seckillId;
				//并没有实现内部序列化操作
				//get->byte[] ->反序列化 ->Object(SecKill)
				//采用自定义序列化
				//protostuff:类型是pojo
			   byte[] bytes=jedis.get(key.getBytes());
			   //缓存中重获取到
			   if(bytes!=null){
				   //传一个Seckill的空对象,根据schema传到了空对象中
				   //比原来压缩的小且快                                                                                                                                                                                 
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
		//set Object(Seckill)->序列化 -> byte[]
		try{
			Jedis jedis=jedisPool.getResource();
			try{
				String key="seckill:"+secKill.getSeckillId();
				//将其转化为字符数组，LinedBuffer是缓存器,给默认大小就好
				byte[] bytes=ProtostuffIOUtil.toByteArray(secKill,schema, 
					LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
		        //超时缓存
				int timeout=60*60;//缓存一小时
				//错误返回错误信息，正确返回OK
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

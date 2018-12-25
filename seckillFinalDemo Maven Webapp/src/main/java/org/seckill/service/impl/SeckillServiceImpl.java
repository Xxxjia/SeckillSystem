package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKillDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.entity.SuccessKill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.joran.conditional.IfAction;

@Service
public class SeckillServiceImpl implements SeckillService {
	//业务层接口的实现类,需要dao的实例对象

	//日志
	private Logger logger=(Logger) LoggerFactory.getLogger(this.getClass());
	
	//注入service依赖
	@Autowired  //@Autowired是spring的注解，自动注入。这里还可用@Resource,@Inject
	private SeckillDao seckillDao;
	
	@Autowired
	private SuccessKillDao successkillDao;
	
	@Autowired
	private RedisDao redisDao;
	
	//一个规定的MD5盐值，是不可逆的，用于验证
	private final String slat="sdhsjk23432HUIsdfd";
	
	public List<SecKill> getSeckillList() {
		//获取商品库存列表
		// TODO Auto-generated method stub
		//列表行数待修改，需要web层前端传数据
		return seckillDao.queryAll(0, 4);
	}

	public SecKill getById(long seckillId) {
		//由商品id获取库存商品
		// TODO Auto-generated method stub
		return seckillDao.queryById(seckillId);
	}

	//秒杀开始时输出秒杀地址接口
	public Exposer exportSeckillUrl(long seckillId) {
	//优化点：缓存优化
	/**
	 * 常用的cache的方法（伪代码）：常写在service层，但应该放dao数据访问层
	 * get from cache
	 * if null
	 *    get db
	 * else
	 *     put cache
	 * locgoin
	 */
		//1.访问redis
		SecKill secKill=redisDao.getSeckill(seckillId);
		if(redisDao==null){
			//2.缓存中不存在，访问数据库
			secKill=seckillDao.queryById(seckillId);
			if(secKill==null){
				//数据库中没有该库存
				return new Exposer(false, seckillId);
			}else{
				//3.将其放入缓存redis中
				redisDao.putSeckill(secKill);
			}
			
		}
		
		
	//	SecKill secKill=seckillDao.queryById(1003);
		//找不到该秒杀
	/*	if(secKill==null){
			return new Exposer(false, seckillId);
		}*/
		//找到了该秒杀，判断是否开始以及是否已超时  Date.getTime():返回该事件的一个毫秒表示
		Date startTime=secKill.getStartTime();
		Date endTime=secKill.getEndTime();
		//系统当前时间
		Date currentTime=new Date();
		//Date.getTime():返回该事件的一个毫秒表示
		//还未开始或者已经超时,返回失败的Exposer
		if(currentTime.getTime()<startTime.getTime()||currentTime.getTime()>endTime.getTime()){
			return new Exposer(false,seckillId, currentTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		//秒杀成功，获得一个成功的Exposer
		String md5=getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}
	
	private String getMD5(long seckillId){
		String base=seckillId+"/"+slat;
		String md5=DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	//执行秒杀操作，只有这个方法有比较多的修改操作，会用到事务
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		// 先验证md5
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		try {
			// 本来是先减库存，再取记录购买行为  
			// 更新成功，即已经成功秒杀了，需要记录购买行为
			// 此处seckillid和phone作为联合主键是唯一的，也就是只能插入一次
			// 当插入多次时,不会更新成功，则返回0，报“重复秒杀”异常
			//插入操作只需要id和phone，state默认为0，而时间则获取当前时间
			//----------------------------------简单优化后
			//为了降低行级锁的持有时间，先update再insert
			Date currentTime = new Date();
			int insertCount = successkillDao.insertSuccessKilled(seckillId, userPhone);

			if (insertCount <= 0) {//重复秒杀了
				throw new RepeatKillException("seckill repeated");
				
			} else {
				//更新，减少库存
				int updateCount= seckillDao.reduceNumber(seckillId, currentTime);
				if (updateCount <= 0) {
					//rollback
					throw new SeckillCloseException("seckill is closed");
				} else {
					//commit
					// 秒杀成功，返回successExcution(秒杀完成后的状态)
					SuccessKill successKill = successkillDao.queryByIdWithSecKill(seckillId, userPhone);
					return new SeckillExecution(seckillId,1, "秒杀成功",successKill);
				}
			}
			//先捕获两个规定的业务层的异常，且因为它们继承seckillException，所以要先写在前面捕获，才会看得出是什么异常了。
		} catch (SeckillCloseException ex1) {
			logger.error(ex1.getMessage(), ex1);
			throw ex1;

		} catch (RepeatKillException ex2) {
			logger.error(ex2.getLocalizedMessage(),ex2);
			throw ex2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// 将所有编译期异常，转化为运行期异常
			throw new SeckillException("seckill inner error:" + e.getMessage());

		}

	}
	public SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5){
		if(md5==null ||!md5.equals(getMD5(seckillId))){
			return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
		}
		Date killTime=new Date();
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("seckillId",seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result",null);
		//执行存储过程，result被复制
		try{
			seckillDao.killByProcedure(map);
			//获取map中的result,如果为空，则返回-2
			int result=MapUtils.getIntValue(map, "result",-2);
			if(result==1){
				//秒杀成功
				SuccessKill sk=successkillDao.queryByIdWithSecKill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,sk);
			}else{
				//内部异常
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
		
	}

}

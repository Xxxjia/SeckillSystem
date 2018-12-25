package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

public interface SeckillService {

	List<SecKill> getSeckillList();
	
	
	//根据id获取库存商品
	SecKill getById(long seckillId);
	
	/**
	 * 秒杀开启时输出秒杀接口地址
	 * 否则输出系统时间和秒杀时间
	 * 用户应该要直到秒杀开始才获得秒杀接口地址，
	 * 如果用户可以通过拼接url地址去获得秒杀接口，就可能会通过一些插件去实现秒杀了
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * 执行秒杀操作
	 * 需要秒杀商品的id，秒杀用户的手机号，以及此时已经暴露了秒杀接口地址，所以需要验证
	 * md5与内部的md5进行比较，看用户的是否被篡改了
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
	    throws SeckillException,RepeatKillException,SeckillCloseException;
	
	/**
	 * 执行秒杀操作  by存储过程
	 * 需要秒杀商品的id，秒杀用户的手机号，以及此时已经暴露了秒杀接口地址，所以需要验证
	 * md5与内部的md5进行比较，看用户的是否被篡改了
	 */
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
	
}

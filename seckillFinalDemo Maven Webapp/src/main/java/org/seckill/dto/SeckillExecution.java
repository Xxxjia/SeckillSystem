package org.seckill.dto;

import org.seckill.entity.SuccessKill;
import org.seckill.enums.SeckillStatEnum;

/**
 * 封装秒杀执行后的结果
 * @author xj
 *
 */
public class SeckillExecution {

	//秒杀商品id
	private long seckillId;
	
	//秒杀结果状态
	private int state;
	
	//秒杀结果说明
	private String stateInfo;
	
	//秒杀成功对象
	private SuccessKill successKill;

	//成功时
	public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKill successKill) {
		this.seckillId = seckillId;
		this.state =statEnum.getState();
		this.stateInfo = statEnum.getStateInfo();
		this.successKill = successKill;
	}
	
	
	//失败时
	public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
		this.seckillId = seckillId;
		this.state = statEnum.getState();
		this.stateInfo =statEnum.getStateInfo();
	}

	


	public SeckillExecution(long seckillId, int state, String stateInfo, SuccessKill successKill) {
		
		this.seckillId = seckillId;
		this.state = state;
		this.stateInfo = stateInfo;
		this.successKill = successKill;
	}


	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public SuccessKill getSuccessKill() {
		return successKill;
	}

	public void setSuccessKill(SuccessKill successKill) {
		this.successKill = successKill;
	}


	
	
}

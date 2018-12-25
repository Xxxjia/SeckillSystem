package org.seckill.dto;

import org.seckill.entity.SuccessKill;
import org.seckill.enums.SeckillStatEnum;

/**
 * ��װ��ɱִ�к�Ľ��
 * @author xj
 *
 */
public class SeckillExecution {

	//��ɱ��Ʒid
	private long seckillId;
	
	//��ɱ���״̬
	private int state;
	
	//��ɱ���˵��
	private String stateInfo;
	
	//��ɱ�ɹ�����
	private SuccessKill successKill;

	//�ɹ�ʱ
	public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKill successKill) {
		this.seckillId = seckillId;
		this.state =statEnum.getState();
		this.stateInfo = statEnum.getStateInfo();
		this.successKill = successKill;
	}
	
	
	//ʧ��ʱ
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

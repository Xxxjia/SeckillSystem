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
	
	
	//����id��ȡ�����Ʒ
	SecKill getById(long seckillId);
	
	/**
	 * ��ɱ����ʱ�����ɱ�ӿڵ�ַ
	 * �������ϵͳʱ�����ɱʱ��
	 * �û�Ӧ��Ҫֱ����ɱ��ʼ�Ż����ɱ�ӿڵ�ַ��
	 * ����û�����ͨ��ƴ��url��ַȥ�����ɱ�ӿڣ��Ϳ��ܻ�ͨ��һЩ���ȥʵ����ɱ��
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * ִ����ɱ����
	 * ��Ҫ��ɱ��Ʒ��id����ɱ�û����ֻ��ţ��Լ���ʱ�Ѿ���¶����ɱ�ӿڵ�ַ��������Ҫ��֤
	 * md5���ڲ���md5���бȽϣ����û����Ƿ񱻴۸���
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
	    throws SeckillException,RepeatKillException,SeckillCloseException;
	
	/**
	 * ִ����ɱ����  by�洢����
	 * ��Ҫ��ɱ��Ʒ��id����ɱ�û����ֻ��ţ��Լ���ʱ�Ѿ���¶����ɱ�ӿڵ�ַ��������Ҫ��֤
	 * md5���ڲ���md5���бȽϣ����û����Ƿ񱻴۸���
	 */
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
	
}

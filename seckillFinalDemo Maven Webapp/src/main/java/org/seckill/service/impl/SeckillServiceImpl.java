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
	//ҵ���ӿڵ�ʵ����,��Ҫdao��ʵ������

	//��־
	private Logger logger=(Logger) LoggerFactory.getLogger(this.getClass());
	
	//ע��service����
	@Autowired  //@Autowired��spring��ע�⣬�Զ�ע�롣���ﻹ����@Resource,@Inject
	private SeckillDao seckillDao;
	
	@Autowired
	private SuccessKillDao successkillDao;
	
	@Autowired
	private RedisDao redisDao;
	
	//һ���涨��MD5��ֵ���ǲ�����ģ�������֤
	private final String slat="sdhsjk23432HUIsdfd";
	
	public List<SecKill> getSeckillList() {
		//��ȡ��Ʒ����б�
		// TODO Auto-generated method stub
		//�б��������޸ģ���Ҫweb��ǰ�˴�����
		return seckillDao.queryAll(0, 4);
	}

	public SecKill getById(long seckillId) {
		//����Ʒid��ȡ�����Ʒ
		// TODO Auto-generated method stub
		return seckillDao.queryById(seckillId);
	}

	//��ɱ��ʼʱ�����ɱ��ַ�ӿ�
	public Exposer exportSeckillUrl(long seckillId) {
	//�Ż��㣺�����Ż�
	/**
	 * ���õ�cache�ķ�����α���룩����д��service�㣬��Ӧ�÷�dao���ݷ��ʲ�
	 * get from cache
	 * if null
	 *    get db
	 * else
	 *     put cache
	 * locgoin
	 */
		//1.����redis
		SecKill secKill=redisDao.getSeckill(seckillId);
		if(redisDao==null){
			//2.�����в����ڣ��������ݿ�
			secKill=seckillDao.queryById(seckillId);
			if(secKill==null){
				//���ݿ���û�иÿ��
				return new Exposer(false, seckillId);
			}else{
				//3.������뻺��redis��
				redisDao.putSeckill(secKill);
			}
			
		}
		
		
	//	SecKill secKill=seckillDao.queryById(1003);
		//�Ҳ�������ɱ
	/*	if(secKill==null){
			return new Exposer(false, seckillId);
		}*/
		//�ҵ��˸���ɱ���ж��Ƿ�ʼ�Լ��Ƿ��ѳ�ʱ  Date.getTime():���ظ��¼���һ�������ʾ
		Date startTime=secKill.getStartTime();
		Date endTime=secKill.getEndTime();
		//ϵͳ��ǰʱ��
		Date currentTime=new Date();
		//Date.getTime():���ظ��¼���һ�������ʾ
		//��δ��ʼ�����Ѿ���ʱ,����ʧ�ܵ�Exposer
		if(currentTime.getTime()<startTime.getTime()||currentTime.getTime()>endTime.getTime()){
			return new Exposer(false,seckillId, currentTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		//��ɱ�ɹ������һ���ɹ���Exposer
		String md5=getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}
	
	private String getMD5(long seckillId){
		String base=seckillId+"/"+slat;
		String md5=DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	//ִ����ɱ������ֻ����������бȽ϶���޸Ĳ��������õ�����
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		// ����֤md5
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		try {
			// �������ȼ���棬��ȡ��¼������Ϊ  
			// ���³ɹ������Ѿ��ɹ���ɱ�ˣ���Ҫ��¼������Ϊ
			// �˴�seckillid��phone��Ϊ����������Ψһ�ģ�Ҳ����ֻ�ܲ���һ��
			// ��������ʱ,������³ɹ����򷵻�0�������ظ���ɱ���쳣
			//�������ֻ��Ҫid��phone��stateĬ��Ϊ0����ʱ�����ȡ��ǰʱ��
			//----------------------------------���Ż���
			//Ϊ�˽����м����ĳ���ʱ�䣬��update��insert
			Date currentTime = new Date();
			int insertCount = successkillDao.insertSuccessKilled(seckillId, userPhone);

			if (insertCount <= 0) {//�ظ���ɱ��
				throw new RepeatKillException("seckill repeated");
				
			} else {
				//���£����ٿ��
				int updateCount= seckillDao.reduceNumber(seckillId, currentTime);
				if (updateCount <= 0) {
					//rollback
					throw new SeckillCloseException("seckill is closed");
				} else {
					//commit
					// ��ɱ�ɹ�������successExcution(��ɱ��ɺ��״̬)
					SuccessKill successKill = successkillDao.queryByIdWithSecKill(seckillId, userPhone);
					return new SeckillExecution(seckillId,1, "��ɱ�ɹ�",successKill);
				}
			}
			//�Ȳ��������涨��ҵ�����쳣������Ϊ���Ǽ̳�seckillException������Ҫ��д��ǰ�沶�񣬲Żῴ�ó���ʲô�쳣�ˡ�
		} catch (SeckillCloseException ex1) {
			logger.error(ex1.getMessage(), ex1);
			throw ex1;

		} catch (RepeatKillException ex2) {
			logger.error(ex2.getLocalizedMessage(),ex2);
			throw ex2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// �����б������쳣��ת��Ϊ�������쳣
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
		//ִ�д洢���̣�result������
		try{
			seckillDao.killByProcedure(map);
			//��ȡmap�е�result,���Ϊ�գ��򷵻�-2
			int result=MapUtils.getIntValue(map, "result",-2);
			if(result==1){
				//��ɱ�ɹ�
				SuccessKill sk=successkillDao.queryByIdWithSecKill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,sk);
			}else{
				//�ڲ��쳣
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
		
	}

}

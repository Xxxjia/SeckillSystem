package org.seckill.service;


import static org.junit.Assert.*;

import java.lang.reflect.Executable;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.classic.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath:spring/springDao-config.xml",
	"classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

	private final Logger logger=(Logger) LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	@Test
	public void testGetSeckillList() {
		//fail("Not yet implemented");
		List<SecKill> list=seckillService.getSeckillList();
		logger.info("list={}",list);
	}

	@Test
	public void testGetById() {
		//fail("Not yet implemented");
		long id=1001;
		SecKill secKill=seckillService.getById(id);
		logger.info("seckill={}",secKill);
	}

	@Test
	public void testExportSeckillUrl() {
		//fail("Not yet implemented");
		long id=1003;
		Exposer exposer=seckillService.exportSeckillUrl(id);
		logger.info("exposer={}",exposer);
		//�޸���ʱ�䣬��ʱ��õ�1001����ɱ�ǳɹ��ģ������һЩ����
		//[exposed=true,
		//md5=86b72cbaa02edb1bf3ee808f0381b177, 
		//seckillId=1001, now=0, start=0, end=0]
	}

	//ִ����ɱ��Ϊ
	@Test
	public void testExecuteSeckill() {
		//fail("Not yet implemented");
		long id=1003;
		long phone=13602805692L;
		String md5="86b72cbaa02edb1bf3ee808f0381b177";
		
		try{
		SeckillExecution execution=seckillService.executeSeckill(id, phone, md5);
		logger.info("result={}",execution);
		//Ϊ�˷�ֹ�����׳��Զ����ҵ����쳣����������ԣ����˺������쳣����д����־
		}catch (RepeatKillException e) {
			logger.error(e.getMessage());
		}catch (SeckillCloseException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	@Test
	public void testSeckillLogic(){//���ж��Ƿ������ɱ��ִ����ɱ���������������ɲ���
		long id = 1003;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if (exposer.isExposed()) {
			logger.info("exposer={}", exposer);
			long phone = 13602805789L;
			String md5 = exposer.getMd5();
			try {
				SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
				logger.info("result={}", execution);
				// Ϊ�˷�ֹ�����׳��Զ����ҵ����쳣����������ԣ����˺������쳣����д����־
			} catch (RepeatKillException e) {
				logger.error(e.getMessage());
			} catch (SeckillCloseException e) {
				logger.error(e.getMessage());
			}
		} else {
			// ��ɱδ����,��������
			logger.warn("exposer={}", exposer);

		}
	}
	
	@Test
	public void testExecuteSeckillProcedure() throws Exception{
		long seckillId=1001;
		long phone=13680238934l;
		//��ȡ��ɱ�ı�¶��ַ
		Exposer exposer=seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			String md5=exposer.getMd5();
			SeckillExecution execution=seckillService.executeSeckillProcedure(seckillId,phone, md5);
		    logger.info(execution.getStateInfo() );
		}
		
	}

}

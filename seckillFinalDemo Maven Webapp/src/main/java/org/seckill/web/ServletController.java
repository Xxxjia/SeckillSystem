package org.seckill.web;



import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.SecKill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.classic.Logger;

@Controller
@RequestMapping("/seckill")  //url:/ģ��/��Դ/{id}/ϸ��
public class ServletController {
	private final Logger logger=(Logger) LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	//model�ṩ����  + jsp =ModelAndView
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		//��ȡ�б�ҳ���б�ҳ��������
		List<SecKill> list=seckillService.getSeckillList();
		model.addAttribute("list",list);
		return "list";   //web-inf/jsp/"list".jsp
	}
	
	
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model){
		//��ȡ����嵥,����ҳ��������.�������������б�ҳ��������ת������ҳ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
		if(seckillId==null){
			//������ʱ���ض��򷵻ص��б�ҳ��
			return "redirect:/seckill/list";
		}
		SecKill secKill=seckillService.getById(seckillId);
		if(secKill==null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill",secKill);
		return "detail";
	}
	
	//ajax�ӿڣ�����json����
	@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})//�������󷽷����ͺ���������
	@ResponseBody//˵��ʱ����json���ͣ����Զ���װ��hson
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
		SeckillResult<Exposer> result;
		
		try{
			//�з���ֵ���ɹ�
			Exposer exposer=seckillService.exportSeckillUrl(seckillId);
			result=new SeckillResult<Exposer>(true, exposer);
		}catch(Exception e){
			//System.out.println("HI");
			logger.error(e.getMessage(),e);
			result=new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}
	
	//ִ����ɱ
	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
			                                       @PathVariable("md5") String md5,//cookie��ȡphone��������֤��requiredΪfalseʹ���ֵ�����Ǳ����
                                     @CookieValue(value="killPhone",required=false) Long phone){
		//spring����֤����ֻ��һ���������������ʵû��Ҫ
		if(phone==null){
			return new SeckillResult<SeckillExecution>(false, "δע��");
		}
	//	SeckillResult<SeckillExecution> result;
		try {
			 SeckillExecution execution=seckillService.executeSeckill(seckillId, phone, md5);
			return new SeckillResult<SeckillExecution>(true, execution);
		}catch(RepeatKillException e1){
			SeckillExecution execution=new  SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
		    return new SeckillResult<SeckillExecution>(true, execution);
			
		}catch(SeckillCloseException ex2){
			SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.END);
			return new SeckillResult<SeckillExecution>(true, execution);
		
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true, execution);
		}
	}
	
	@RequestMapping(value="/time/now",method=RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date now=new Date();
		return new SeckillResult<Long>(true, now.getTime());
	}
	
}

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
@RequestMapping("/seckill")  //url:/模块/资源/{id}/细分
public class ServletController {
	private final Logger logger=(Logger) LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	//model提供数据  + jsp =ModelAndView
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public String list(Model model){
		//获取列表页，列表页的请求处理
		List<SecKill> list=seckillService.getSeckillList();
		model.addAttribute("list",list);
		return "list";   //web-inf/jsp/"list".jsp
	}
	
	
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model){
		//获取库存清单,详情页的请求处理.不符合则请求到列表页；符合则转到详情页。；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；；
		if(seckillId==null){
			//不存在时，重定向返回到列表页上
			return "redirect:/seckill/list";
		}
		SecKill secKill=seckillService.getById(seckillId);
		if(secKill==null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill",secKill);
		return "detail";
	}
	
	//ajax接口，返回json对象
	@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})//告诉请求方法类型和数据类型
	@ResponseBody//说明时返回json类型，会自动包装成hson
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
		SeckillResult<Exposer> result;
		
		try{
			//有返回值，成功
			Exposer exposer=seckillService.exportSeckillUrl(seckillId);
			result=new SeckillResult<Exposer>(true, exposer);
		}catch(Exception e){
			//System.out.println("HI");
			logger.error(e.getMessage(),e);
			result=new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}
	
	//执行秒杀
	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
			                                       @PathVariable("md5") String md5,//cookie中取phone，先做验证，required为false使这个值并不是必须的
                                     @CookieValue(value="killPhone",required=false) Long phone){
		//spring的验证，但只有一个参数的情况下其实没必要
		if(phone==null){
			return new SeckillResult<SeckillExecution>(false, "未注册");
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

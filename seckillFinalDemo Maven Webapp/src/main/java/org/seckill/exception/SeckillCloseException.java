package org.seckill.exception;



/**
 * 秒杀关闭异常（如时间到、库存完不应该再执行秒杀）
 * @author xj
 *
 */
public class SeckillCloseException extends SeckillException {

	public SeckillCloseException(String message){
		super(message);
	}
	
	public SeckillCloseException(String message,Throwable cause){
		super(message,cause);
	}
	
}

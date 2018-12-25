package org.seckill.exception;

/**
 * 所有秒杀业务异常
 * @author xj
 *
 */
public class SeckillException extends RuntimeException{
	public SeckillException(String message){
		super(message);
	}
	
	public SeckillException(String message,Throwable cause){
		super(message,cause);
	}

}

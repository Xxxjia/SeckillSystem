package org.seckill.exception;

/**
 * ������ɱҵ���쳣
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

package org.seckill.exception;



/**
 * ��ɱ�ر��쳣����ʱ�䵽������겻Ӧ����ִ����ɱ��
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

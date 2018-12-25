package org.seckill.dto;

/**
 * 秒杀请求是否成功的结果
 * 封装所有json结果
 * 作为所有ajax请求返回类型
 * @author xj
 *
 */
public class SeckillResult<T> {
	
	private boolean success;
	
	private T data;//成功时作为返回数据;或者是错误时返回一个错误的execution(id，seckillEnum.自定义的异常类型)
	
	private String error;//失败时作为返回的错误提示
	
	
	public SeckillResult(boolean success, T data) {
		super();
		this.success = success;
		this.data = data;
	}
	public SeckillResult(boolean success, String error) {
		super();
		this.success = success;
		this.error = error;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
	

}

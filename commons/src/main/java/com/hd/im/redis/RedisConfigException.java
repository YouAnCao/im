package com.hd.im.redis;

public class RedisConfigException extends RuntimeException {

	private static final long serialVersionUID = -3053408151715926608L;
	private String message;

	public RedisConfigException() {

	}

	public RedisConfigException(String message) {
		super(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

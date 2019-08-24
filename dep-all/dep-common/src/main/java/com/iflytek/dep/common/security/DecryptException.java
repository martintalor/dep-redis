package com.iflytek.dep.common.security;

public class DecryptException extends Exception {

	private static final long serialVersionUID = 1L;

	public DecryptException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public DecryptException(Throwable e) {
		super(e);
	}
	
	public DecryptException(String msg) {
		super(msg);
	}
	
}

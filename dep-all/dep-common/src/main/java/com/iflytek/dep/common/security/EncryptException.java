package com.iflytek.dep.common.security;

public class EncryptException extends Exception {

	private static final long serialVersionUID = 1L;

	public EncryptException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public EncryptException(Throwable e) {
		super(e);
	}
	
	public EncryptException(String msg) {
		super(msg);
	}
}

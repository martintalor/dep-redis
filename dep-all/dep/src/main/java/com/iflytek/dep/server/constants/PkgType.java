package com.iflytek.dep.server.constants;

public enum PkgType {

	DATA(1),
	ACK(2),
	MAINDATA(3);
	
    private int value;
	
    PkgType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;  
	}
}

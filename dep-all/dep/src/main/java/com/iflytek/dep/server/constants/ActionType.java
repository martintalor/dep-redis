package com.iflytek.dep.server.constants;

public enum ActionType {

	UP(1),
	DOWN(2);
	
    private int value;
	
    ActionType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;  
	}
}

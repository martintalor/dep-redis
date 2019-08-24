package com.iflytek.dep.server.constants;

public enum ExchangeNodeType {

	MAIN(0),
	LEAF(1),
	ROUTE(2);
	
    private int value;
	
    ExchangeNodeType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;  
	}
}

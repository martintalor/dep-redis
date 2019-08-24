package com.iflytek.dep.server.constants;

public enum PkgStatus {

	DIR_CAREAT(1),
	FILE_LOADED(2),
	COMPRSS_BEFORE(3),
	COMPRESS_AFTER(4),
	ENCRYPT_BEFORE(5),
	ENCRYPT_AFTER(6),
	UPLOAD_BEFOR(7),
	UPLOAD_AFTER(8);
	
    private int value;
	
	PkgStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;  
	}
}

package com.iflytek.dep.server.section;

import com.iflytek.dep.common.utils.ToStringModel;

import java.util.concurrent.ConcurrentHashMap;

public class SectionResult extends ToStringModel {

	private boolean ok = false;
	
	private String key = null;

	private ConcurrentHashMap<String,Object> map=null;

	public ConcurrentHashMap<String, Object> getMap() {
		return map;
	}

	public void setMap(ConcurrentHashMap<String, Object> map) {
		this.map = map;
	}

	public void setOk(boolean ok) {

		this.ok = ok;
	}

	public SectionResult(boolean rst) {
		this.ok = rst;
	}
	

	public boolean isOk() {
		return ok;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}

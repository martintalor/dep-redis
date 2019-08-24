package com.iflytek.dep.server.section;


import com.iflytek.dep.server.constants.PkgStatus;

public interface Status {
	
	public void update(String pkgId, PkgStatus status);

}

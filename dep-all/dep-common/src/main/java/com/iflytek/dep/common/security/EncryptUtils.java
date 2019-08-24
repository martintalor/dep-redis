package com.iflytek.dep.common.security;

import java.io.File;

public class EncryptUtils {

	public static String getFileName(String filePath) {
		File f = new File(filePath.trim());
		return f.getName();
	}
	
	public static String getFileDir(String filePath) {
		File f = new File(filePath.trim());
		return f.getParent();
	}

}

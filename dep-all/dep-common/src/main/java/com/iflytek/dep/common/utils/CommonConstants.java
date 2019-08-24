package com.iflytek.dep.common.utils;

public class CommonConstants {
	public interface RESPONSE_INFO {
		public final static String ERROR_MESSAGE = "";
		//接口请求成功代码
		public final static String SUCCESS = "1";
		//接口请求成功代码
		public final static String FAILURE = "0";
	}

	public interface ETL_FILE_SUFFIX {
		//injob标识文件扩展名
		public final static String IN_JOB = ".hd";
		//索引文件扩展名
		public final static String INDEX = ".at";
		//非结构化文件前缀
		public final static String NOTSTRUCT = "NOTSTRUCT";
	}

	//文件相关
	public interface FILE_INFO {
		//地址分隔符
		public final static String FILESPLIT = "/";
		//ZIP包后缀
		public final static String ZIP = ".zip";
		//名称下标
		public final static String SPILT = "_";
	}


}

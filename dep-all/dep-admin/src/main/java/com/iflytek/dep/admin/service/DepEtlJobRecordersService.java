package com.iflytek.dep.admin.service;

import com.github.pagehelper.PageInfo;
import com.iflytek.dep.admin.model.DepEtlJobRecorders;
import com.iflytek.dep.admin.model.dto.DepEtlJobRecordersDto;

import java.util.Map;

public interface DepEtlJobRecordersService {

	/**
	 * 获取所有任务状态
	 *
	 * @return
	 */
	Map<String, Object> getJobStatusNumber();
	/**
	 * 获取所有任务状态
	 *
	 * @return
	 */
	Map<String, Object> getOneJobStatusNumber(String packageId);

	/**
	 * 统计所有job执行情况
	 * @param param
	 * @return
	 */

	PageInfo<DepEtlJobRecorders> getEtlJobRecorders(DepEtlJobRecordersDto param);

	/**
	 * 获取单个job执行情况
	 * @param param
	 * @return
	 */
	PageInfo<DepEtlJobRecorders> getEtlOneJobRecorders(DepEtlJobRecordersDto param);
}

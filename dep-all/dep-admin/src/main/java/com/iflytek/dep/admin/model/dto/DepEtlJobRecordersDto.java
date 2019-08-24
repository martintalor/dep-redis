package com.iflytek.dep.admin.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public class DepEtlJobRecordersDto extends BaseDto {
	private BigDecimal jobType;
	private String jobId;

	private Date startTime;

	private Date endTime;

	private String jobName;

	private String packageId;


	private BigDecimal rowNumber;

	private BigDecimal totalError;
	private BigDecimal totalSuccess;
	private BigDecimal totalNumber;

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public BigDecimal getTotalSuccess() {
		return totalSuccess;
	}

	public void setTotalSuccess(BigDecimal totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public BigDecimal getTotalError() {
		return totalError;
	}

	public void setTotalError(BigDecimal totalError) {
		this.totalError = totalError;
	}

	public BigDecimal getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}

	public BigDecimal getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(BigDecimal rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	private BigDecimal jobStatus;

	public BigDecimal getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(BigDecimal jobStatus) {
		this.jobStatus = jobStatus;
	}

	public BigDecimal getJobType() {
		return jobType;
	}

	public void setJobType(BigDecimal jobType) {
		this.jobType = jobType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}

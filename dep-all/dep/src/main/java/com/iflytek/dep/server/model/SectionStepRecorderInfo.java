package com.iflytek.dep.server.model;

public class SectionStepRecorderInfo {
	private Integer totalSection;
	private Integer successSection;
	private String packageId;

	public Integer getTotalSection() {
		return totalSection;
	}

	public void setTotalSection(Integer totalSection) {
		this.totalSection = totalSection;
	}

	public Integer getSuccessSection() {
		return successSection;
	}

	public void setSuccessSection(Integer successSection) {
		this.successSection = successSection;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
}

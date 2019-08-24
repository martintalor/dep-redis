package com.iflytek.dep.server.utils;

import com.google.gson.Gson;
import com.iflytek.dep.common.utils.RandomGUID;
import com.iflytek.dep.server.mapper.SectionStepRecordersMapper;
import com.iflytek.dep.server.model.SectionStepRecorders;
import com.iflytek.dep.server.section.SectionNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SectionUtils {
	private static Logger logger = LoggerFactory.getLogger(SectionUtils.class);

	@Autowired
	SectionStepRecordersMapper sectionStepRecordersMapper;


	public static BigDecimal getTotalSectionLength(SectionNode sectionNode) {
		BigDecimal result = new BigDecimal(0);
		SectionNode temp = sectionNode;
		while (temp != null) {
			result = result.add(new BigDecimal(1));
			temp = temp.getNext();
		}
		return result;
	}

	public static SectionNode checkSetionStep(List<SectionStepRecorders> sectionRecorderList, SectionNode sectionNodeInner, ConcurrentHashMap<String, Object> param) {
		SectionNode tempSectionNode = null;
		if (sectionRecorderList.size() > 0) {
			SectionNode temp = sectionNodeInner;
			boolean isAllDeal = false;
			while (temp != null) {
				for (SectionStepRecorders sectionStepRecorders : sectionRecorderList) {
					//找到section执行开始记录
					if (sectionStepRecorders.getSectionName().equals(temp.getCurrent().getClass() + "")
							&& sectionStepRecorders.getDoactResult().toBigInteger().intValue() == 0) {

						boolean findSectionResultSuccess = false;
						for (SectionStepRecorders sectionStepRecordersItem : sectionRecorderList) {
							//找到section执行结束记录
							if (sectionStepRecorders.getSectionName().equals(sectionStepRecordersItem.getSectionName())
									&& sectionStepRecordersItem.getDoactResult().toBigInteger().intValue() == 1) {
								//当前这个section执行完成了
								findSectionResultSuccess = true;
								param.clear();
								Gson gson = new Gson();
								param.putAll(gson.fromJson(sectionStepRecordersItem.getSectionParam(), ConcurrentHashMap.class));
								//发现该包执行过当前的section
								tempSectionNode = temp.getNext();
								isAllDeal = true;
								break;
							}
						}
						if (!findSectionResultSuccess) {
							param.clear();
							Gson gson = new Gson();
							param.putAll(gson.fromJson(sectionStepRecorders.getSectionParam(), ConcurrentHashMap.class));
						}
					}

				}
				temp = temp.getNext();

			}
			//如果一个都没成功（排除掉所有都成功的可能）
			if (tempSectionNode == null && !isAllDeal) {
				tempSectionNode = sectionNodeInner;

				for (SectionStepRecorders sectionStepRecorders : sectionRecorderList) {
					//找到section执行开始记录
					if (sectionStepRecorders.getSectionName().equals(tempSectionNode.getCurrent().getClass() + "")
							&& sectionStepRecorders.getDoactResult().toBigInteger().intValue() == 0) {
						param.clear();
						Gson gson = new Gson();
						param.putAll(gson.fromJson(sectionStepRecorders.getSectionParam(), ConcurrentHashMap.class));
						break;
					}
				}
			}
		} else {
			tempSectionNode = sectionNodeInner;
		}

		return tempSectionNode;
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void insertSectionStepRecorders(ConcurrentMap param, BigDecimal doactResult, String sectionName, String
			jobId, BigDecimal sectionTotalNumber, BigDecimal direction) {
        String tempSectionName = sectionName;
        int sectionNameIndex = -1;
        // 如果SectionName中有$符，截取调后续字段内容 -- modify by jzkan，20190505
        if((sectionNameIndex = tempSectionName.indexOf("$")) != -1){
            tempSectionName = tempSectionName.substring(0,sectionNameIndex);
            logger.debug("SectionName has $, substring it : " + tempSectionName);
        }
		SectionStepRecorders sectionStepRecorders = new SectionStepRecorders();
		sectionStepRecorders.setId(RandomGUID.getGuid());
		sectionStepRecorders.setSectionParam(new Gson().toJson(param));
		sectionStepRecorders.setDoactResult(doactResult);
		sectionStepRecorders.setSectionName(tempSectionName);
		sectionStepRecorders.setPackageId(jobId);
		sectionStepRecorders.setTotalSectionNumber(sectionTotalNumber);
		sectionStepRecorders.setDirection(direction);
		sectionStepRecordersMapper.insertSelective(sectionStepRecorders);
	}
}

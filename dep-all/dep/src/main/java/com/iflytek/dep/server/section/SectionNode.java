package com.iflytek.dep.server.section;

import com.iflytek.dep.common.utils.ToStringModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Section链条，由SectionNodeBuilder构建
 * @author Kevin
 *
 */

@Scope("prototype")
@Component
public class SectionNode extends ToStringModel {
	
	private Section current;
	private SectionNode next;
	
	public SectionNode(Section current) {
		this.current = current;
	}
	public SectionNode() {}
	public SectionNode next(Section next) {
		SectionNode node = new SectionNode(next);
		this.next = node;
		return node;
	}
	
	public Section getCurrent() {
		return current;
	}

	public void setCurrent(Section current) {
		this.current = current;
	}

	public SectionNode getNext() {
		return next;
	}

	public void setNext(SectionNode next) {
		this.next = next;
	}
	
}

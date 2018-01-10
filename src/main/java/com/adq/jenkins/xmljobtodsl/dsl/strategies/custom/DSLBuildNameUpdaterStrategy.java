package com.adq.jenkins.xmljobtodsl.dsl.strategies.custom;

import com.adq.jenkins.xmljobtodsl.parsers.PropertyDescriptor;
import com.adq.jenkins.xmljobtodsl.dsl.strategies.DSLObjectStrategy;

public class DSLBuildNameUpdaterStrategy extends DSLObjectStrategy {

	public DSLBuildNameUpdaterStrategy(int tabs, PropertyDescriptor propertyDescriptor, String name) {
		super(tabs, propertyDescriptor, name, false);

		PropertyDescriptor buildName = new PropertyDescriptor("buildName", propertyDescriptor);
		propertyDescriptor.getProperties().add(0, buildName);
		initChildren(propertyDescriptor);
	}
}

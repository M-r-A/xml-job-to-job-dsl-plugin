package com.adq.jenkins.xmljobtodsl.dsl.strategies;

import com.adq.jenkins.xmljobtodsl.PropertyDescriptor;

import java.util.List;

public class DSLConstantStrategy extends AbstractDSLStrategy {

    private final String name;
    private final PropertyDescriptor propertyDescriptor;

    public DSLConstantStrategy(PropertyDescriptor propertyDescriptor, String name) {
        super(propertyDescriptor, 0);
        this.name = name;
        this.propertyDescriptor = propertyDescriptor;
    }

    @Override
    public String toDSL() {
        return String.format(getSyntaxProperties().getProperty("syntax.string_variable"),
                name, printValueAccordingOfItsType(propertyDescriptor.getValue()));
    }
}

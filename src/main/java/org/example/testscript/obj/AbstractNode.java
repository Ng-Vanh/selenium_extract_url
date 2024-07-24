package org.example.testscript.obj;

import org.example.testscript.Property;

import java.util.List;

public abstract class AbstractNode {
    private String name;

    private List<Property> propertyList;
    public AbstractNode() {

    }
    public AbstractNode(String name, List<Property> pros) {
        this.name = name;
        this.propertyList = pros;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }
    public Property getProperty(String key) {
        for (Property prop : propertyList) {
            if (prop.getKey().equals(key)) {
                return prop;
            }
        }
        return null;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String result = "AbstractNode{" +
                "name='" + name + '\'' +
                ", propertyList= ";
        for(Property prop : propertyList) {
            result += "Key : "+ prop.getKey() +", Value: " + prop.getValue() + ". " ;
        }
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }
    public abstract String generateTestscript();

    public abstract String toSeleniumCode();

}

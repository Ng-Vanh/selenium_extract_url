package org.example.testscript.obj;

import org.example.testscript.Property;

import java.util.List;

public class LinkObject extends AbstractNode {
    public LinkObject(String name, List<Property> pros) {
        super(name,pros);
    }
    @Override
    public String generateTestscript() {
        return null;
    }

    @Override
    public String toSeleniumCode() {
        StringBuilder code = new StringBuilder();
        for (Property prop : getPropertyList()) {
            if (prop.getKey().equals("Action") && prop.getValue().equals("open")) {
                code.append("driver.get(\"").append(this.getProperty("Url").getValue()).append("\");\n");
            }
        }
        return code.toString();
    }
}
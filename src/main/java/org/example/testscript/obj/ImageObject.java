package org.example.testscript.obj;

import org.example.testscript.Property;

import java.util.List;

public class ImageObject extends AbstractNode {
    public ImageObject(String name, List<Property> pros) {
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
            code.append("driver.get(\"").append(prop.getValue()).append("\");\n");
        }
        return code.toString();
    }
}
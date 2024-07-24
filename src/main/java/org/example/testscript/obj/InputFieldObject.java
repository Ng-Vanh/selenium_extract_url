package org.example.testscript.obj;

import org.example.testscript.Property;

import java.util.List;

public class InputFieldObject extends AbstractNode {
    private String url;

    public InputFieldObject(String name, List<Property> properties, String url) {
        super(name, properties);
        this.url = url;
    }

    public InputFieldObject(String name, List<Property> properties) {
        super(name, properties);
    }

    @Override
    public String generateTestscript() {
        return null;
    }

    @Override
    public String toSeleniumCode() {
        StringBuilder code = new StringBuilder();
        for (Property prop : this.getPropertyList()) {
            code.append("driver.findElement(By.name(\"").append(prop.getKey()).append("\")).sendKeys(\"").append(prop.getValue()).append("\");\n");
        }
        return code.toString();
    }
}
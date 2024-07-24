package org.example.testscript.obj;

import org.example.testscript.Property;

import java.util.List;

public class ButtonObject extends AbstractNode{
    private String url;

    public ButtonObject(String name, List<Property> properties, String url) {
        super(name, properties);
        this.url = url;
    }
    public ButtonObject(String name, List<Property> pros) {
        super(name,pros);
    }
    @Override
    public String generateTestscript() {
        return null;
    }

    @Override
    public String toSeleniumCode() {
        StringBuilder code = new StringBuilder();
        for (Property prop : this.getPropertyList()) {
            code.append("driver.findElement(By.name(\"").append(prop.getValue()).append("\")).click();\n");
        }
        return code.toString();
    }
}

package org.example.TreeDOM;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.*;

import static org.example.XPathCollector.XPathCollector.driver;
import static org.example.XPathCollector.XPathCollector.getElementAttributes;

class DOMTreeBuilder {

    public static DomNode buildDOMTree(List<String> xpaths) {
        WebElement rootElement = driver.findElement(By.xpath("/html"));
        Map<String, String> rootAttributes = getElementAttributes(driver, rootElement);
        Point rootPosition = rootElement.getLocation();
        DomNode root = new DomNode("html", rootAttributes, 1, rootPosition);

        for (String xpath : xpaths) {
            addXPathToTree(root, xpath);
        }
        return root;
    }

    private static void addXPathToTree(DomNode root, String xpath) {
        WebElement element = driver.findElement(By.xpath(xpath));
        Map<String,String> attr = getElementAttributes(driver, element);
        Point elementPosition = element.getLocation();
        String[] parts = xpath.split("/");
        DomNode current = root;

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int nthChild = getNthChild(part);
            String tagName = getTagName(part);

            DomNode child = findChild(current, tagName, nthChild);
            if (child == null) {
                child = new DomNode(tagName, attr, nthChild, elementPosition);
                current.addChild(child);
            }
            current = child;
        }
    }
    private static String getTagName(String part) {
        return part.replaceAll("\\[.*\\]", "");
    }

    private static int getNthChild(String part) {
        if (part.contains("[")) {
            return Integer.parseInt(part.replaceAll(".*\\[|\\].*", ""));
        } else {
            return 1;
        }
    }

    private static DomNode findChild(DomNode node, String tagName, int nthChild) {
        for (DomNode child : node.getChildren()) {
            if (child.getTagName().equals(tagName) && child.getNthChild() == nthChild) {
                return child;
            }
        }
        return null;
    }
}
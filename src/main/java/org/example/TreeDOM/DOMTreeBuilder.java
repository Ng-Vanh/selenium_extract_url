package org.example.TreeDOM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.List;

import static org.example.XPathCollector.XPathCollector.driver;
import static org.example.XPathCollector.XPathCollector.getElementAttributes;

class DOMTreeBuilder {

    public static DomNode buildDOMTree(List<String> xpaths) {
        DomNode root = new DomNode("root", new HashMap<>(), 0);
        for (String xpath : xpaths) {
            addXPathToTree(root, xpath);
        }
        return root;
    }

    private static void addXPathToTree(DomNode root, String xpath) {
        WebElement element = driver.findElement(By.xpath(xpath));
        Map<String,String> attr = getElementAttributes(driver,element);
        String[] parts = xpath.split("/");
        DomNode current = root;

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int nthChild = getNthChild(part);
            String tagName = getTagName(part);

            DomNode child = findChild(current, tagName, nthChild);
            if (child == null) {
                child = new DomNode(tagName, attr, nthChild);
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

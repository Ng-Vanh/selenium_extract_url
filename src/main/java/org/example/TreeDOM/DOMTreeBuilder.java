package org.example.TreeDOM;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.*;

import static org.example.XPathCollector.XPathCollector.*;

public class DOMTreeBuilder {
    private static DomNode root;

    public DOMTreeBuilder(DomNode root) {
        DOMTreeBuilder.root = root;
    }

    public static DomNode buildDOMTree(String url) {
        List<String> xpaths = getAllXPaths(url);
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
        Map<String, String> attr = getElementAttributes(driver, element);
        Point elementPosition = element.getLocation();
        String id = element.getAttribute("id");
        String className = element.getAttribute("class");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String content = (String) js.executeScript("return arguments[0].innerText;", element);
        DomNode current = root;

        String[] parts = xpath.split("/");

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int nthChild = getNthChild(part);
            String tagName = getTagName(part);

            DomNode child = findChild(current, tagName, nthChild);
            if (child == null) {
                if (i == parts.length - 1) {
                    child = new DomNode(tagName, id, attr, nthChild, elementPosition, content, className);
                } else {
                    child = new DomNode(tagName, null, null, nthChild, null, null, null);
                }
                current.addChild(child);
            }
            current = child;
        }
    }
    private static String getTagName(String part) {
        return part.replaceAll("\\[.*\\]", "");
    }

    private static int getNthChild(String part) {
        return part.contains("[") ? Integer.parseInt(part.replaceAll(".*\\[|\\].*", "")) : 1;
    }

    private static DomNode findChild(DomNode node, String tagName, int nthChild) {
        return node.getChildren().stream()
                .filter(child -> child.getTagName().equals(tagName) && child.getNthChild() == nthChild)
                .findFirst().orElse(null);
    }

    public static List<DomNode> findNodeCondition(List<String> conditions, DomNode root) {
        List<DomNode> matchingNodes = new ArrayList<>();
        findNodeConditionInTree(root, conditions, matchingNodes);
        return matchingNodes;
    }

    private static void findNodeConditionInTree(DomNode node, List<String> conditions, List<DomNode> matchingNodes) {
        if (matchesConditions(node, conditions)) {
            matchingNodes.add(node);
        }
        for (DomNode child : node.getChildren()) {
            findNodeConditionInTree(child, conditions, matchingNodes);
        }
    }

    private static boolean matchesConditions(DomNode node, List<String> conditions) {
        Map<String, String> attributes = node.getAttributes();
        String content = node.getContent();
        String id = node.getId();
        String name = attributes != null ? attributes.get("name") : null;
        String classAttr = attributes != null ? attributes.get("class") : null;
        String type = attributes != null ? attributes.get("type") : null;
        String placeholder = attributes != null ? attributes.get("placeholder") : null;
        String data = attributes != null ? attributes.get("data") : null;
        String tagName = node.getTagName();

        for (String condition : conditions) {
            if (matchesCondition(condition, id, name, classAttr, type, placeholder, data, tagName, content)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesCondition(String condition, String... attributes) {
        for (String attribute : attributes) {
            if (attribute != null && attribute.contains(condition)) {
                return true;
            }
        }
        return false;
    }
}

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
        this.root = root;
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

        // Iterate over each part of the XPath to create or find corresponding nodes in the tree
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int nthChild = getNthChild(part);
            String tagName = getTagName(part);

            DomNode child = findChild(current, tagName, nthChild);
            if (child == null) {
                // Only add the id, className, and attributes at the last part of the XPath
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
    public static List<DomNode> findNodeCondition(List<String> conditions, DomNode root) {
        List<DomNode> matchingNodes = new ArrayList<>();
        findNodeConditionInTree(root, conditions, matchingNodes);
        return matchingNodes;
    }

    private static boolean findNodeConditionInTree(DomNode node, List<String> conditions, List<DomNode> matchingNodes) {
        // Check if the current node meets the conditions
        boolean matched = matchesConditions(node, conditions);
        if (matched) {
            matchingNodes.add(node);
            return true; // Early termination on finding a match
        }

        // Recursively check children
        for (DomNode child : node.getChildren()) {
            if (findNodeConditionInTree(child, conditions, matchingNodes)) {
                return true; // Early termination on finding a match
            }
        }
        return false;
    }

    private static boolean matchesConditions(DomNode node, List<String> conditions) {
        Map<String, String> attributes = node.getAttributes();
        String content = node.getContent(); // Assuming getContent() exists in DomNode
        String id = node.getId();
        String name = attributes != null ? attributes.get("name") : null;
        String classAttr = attributes != null ? attributes.get("class") : null;
        String type = attributes != null ? attributes.get("type") : null;
        String placeholder = attributes != null ? attributes.get("placeholder") : null;
        String data = attributes != null ? attributes.get("data") : null;
        String tagName = node.getTagName();

        for (String condition : conditions) {
            if (id != null && id.contains(condition)) {
                System.out.println("id");
                return true;
            }
            if (name != null && name.contains(condition)) {
                System.out.println("name");
                return true;
            }
            if (classAttr != null && classAttr.contains(condition)) {
                System.out.println("class");
                return true;
            }
            if (type != null && type.contains(condition)) {
                System.out.println("type");
                return true;
            }
            if (placeholder != null && placeholder.contains(condition)) {
                System.out.println("placeholder");
                return true;
            }
            if (data != null && data.contains(condition)) {
                System.out.println("data");
                return true;
            }
            if (tagName != null && tagName.contains(condition)) {
                System.out.println("tagname");
                return true;
            }
            if (content != null && content.contains(condition)) {
                System.out.println("content");
                return true;
            }
        }
        return false;
    }

}
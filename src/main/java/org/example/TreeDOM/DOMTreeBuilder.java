package org.example.TreeDOM;

import org.apache.commons.math3.linear.RealVector;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;


import java.util.*;

import static org.example.XPathCollector.XPathCollector.*;

public class DOMTreeBuilder {
    private static final double SIMILARITY_THRESHOLD = 0.7;
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


    public static DomNode findNodeCondition(List<String> conditions, DomNode root) {
        for (String condition : conditions) {
            VectorUtils.buildVocabulary(condition);
        }

        return findBestMatchNodeInTree(root, conditions);
    }

    private static DomNode findBestMatchNodeInTree(DomNode node, List<String> conditions) {
        double bestSimilarity = computeNodeSimilarity(node, conditions);
        DomNode bestNode = node;

        for (DomNode child : node.getChildren()) {
            DomNode childBestNode = findBestMatchNodeInTree(child, conditions);
            double childSimilarity = computeNodeSimilarity(childBestNode, conditions);

            if (childSimilarity > bestSimilarity) {
                bestSimilarity = childSimilarity;
                bestNode = childBestNode;
            }
        }

        return bestNode;
    }

    private static double computeNodeSimilarity(DomNode node, List<String> conditions) {
        double bestSimilarity = 0.0;

        Map<String, String> attributes = node.getAttributes();
        String[] nodeData = {
                node.getId(),
                node.getTagName(),
                node.getContent(),
                attributes != null ? attributes.get("name") : null,
                attributes != null ? attributes.get("class") : null,
                attributes != null ? attributes.get("type") : null,
                attributes != null ? attributes.get("placeholder") : null,
                attributes != null ? attributes.get("data") : null
        };

        for (String condition : conditions) {
            RealVector conditionVector = VectorUtils.toRealVector(condition);
            for (String attribute : nodeData) {
                if (attribute != null) {
                    RealVector attributeVector = VectorUtils.toRealVector(attribute);
                    double similarity = VectorUtils.cosineSimilarity(conditionVector, attributeVector);
                    if (similarity > bestSimilarity) {
                        bestSimilarity = similarity;
                    }
                }
            }
        }
        return bestSimilarity;
    }



//    public static List<DomNode> findNodeCondition(List<String> conditions, DomNode root) {
//        List<DomNode> matchingNodes = new ArrayList<>();
//        findNodeConditionInTree(root, conditions, matchingNodes);
//        return matchingNodes;
//    }
//
//    private static void findNodeConditionInTree(DomNode node, List<String> conditions, List<DomNode> matchingNodes) {
//        if (matchesConditions(node, conditions)) {
//            matchingNodes.add(node);
//        }
//        for (DomNode child : node.getChildren()) {
//            findNodeConditionInTree(child, conditions, matchingNodes);
//        }
//    }
//
//    private static boolean matchesConditions(DomNode node, List<String> conditions) {
//        Map<String, String> attributes = node.getAttributes();
//        String content = node.getContent();
//        String id = node.getId();
//        String name = attributes != null ? attributes.get("name") : null;
//        String classAttr = attributes != null ? attributes.get("class") : null;
//        String type = attributes != null ? attributes.get("type") : null;
//        String placeholder = attributes != null ? attributes.get("placeholder") : null;
//        String data = attributes != null ? attributes.get("data") : null;
//        String tagName = node.getTagName();
//
//        for (String condition : conditions) {
//            if (matchesCondition(condition, id, name, classAttr, type, placeholder, data, tagName, content)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private static boolean matchesCondition(String condition, String... attributes) {
//        for (String attribute : attributes) {
//            if (attribute != null && attribute.contains(condition)) {
//                return true;
//            }
//        }
//        return false;
//    }
}

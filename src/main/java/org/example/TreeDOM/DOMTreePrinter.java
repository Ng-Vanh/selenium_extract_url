package org.example.TreeDOM;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.Point;

import java.util.List;
import java.util.Map;

import static org.example.XPathCollector.XPathCollector.driver;
import static org.example.XPathCollector.XPathCollector.getAllXPaths;
import static org.example.XPathCollector.XPathCollector.URL;

public class DOMTreePrinter {
    public static void printDomTree(DomNode node, int level) {
        if (node == null) {
            return;
        }
        System.out.println("===========START==============");
        System.out.println("  ".repeat(level) + node.getTagName() + (node.getNthChild() > 1 ? "[" + node.getNthChild() + "]" : ""));
        String fullXpath = node.getFullXPath();
        System.out.println("Full Xpath: " + fullXpath);

        // In ra id của phần tử nếu có
        if (node.getId() != null && !node.getId().isEmpty()) {
            System.out.println("  ".repeat(level + 1) + "ID: " + node.getId());
        }
        if (node.getContent() != null && !node.getContent().isEmpty()) {
            System.out.println("  ".repeat(level + 1) + "TextContent: " + node.getContent());
        }


        // In ra vị trí của phần tử
//        Point absolutePosition = node.getAbsolutePosition();
//        String relativePositionString = getRelativePosition(node);
//        System.out.println("  ".repeat(level + 1) + "Relative Position: " + relativePositionString);

        for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            System.out.println("  ".repeat(level + 1) + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        System.out.println("===========END==============");
        for (DomNode child : node.getChildren()) {
            printDomTree(child, level + 1);
        }
    }

    public static void main(String[] args) {


        DomNode root = DOMTreeBuilder.buildDOMTree(URL);

        printDomTree(root, 0);

        driver.quit();
    }

    private static String getPosition(Point point) {
        int x = point.getX();
        int y = point.getY();

        if (y < 100) {
            return "Top";
        } else if (x < 100) {
            return "Left";
        } else if (x > 1000) {
            return "Right";
        } else if (y > 1000) {
            return "Bottom";
        } else {
            return "Center";
        }
    }

    private static String getRelativePosition(DomNode node) {
        Point relativePosition = node.getRelativePosition();
        if (relativePosition == null) {
            return "N/A";
        }

        int x = relativePosition.getX();
        int y = relativePosition.getY();

        if (y < 0) {
            return "Top";
        } else if (y > 0) {
            return "Bottom";
        } else if (x < 0) {
            return "Left";
        } else if (x > 0) {
            return "Right";
        } else {
            return "Same position";
        }
    }
}

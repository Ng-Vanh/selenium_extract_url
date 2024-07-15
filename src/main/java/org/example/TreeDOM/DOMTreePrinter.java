package org.example.TreeDOM;

import java.util.List;
import java.util.Map;

import static org.example.XPathCollector.XPathCollector.driver;
import static org.example.XPathCollector.XPathCollector.getAllXPaths;

public class DOMTreePrinter {
    public static void printDomTree(DomNode node, int level) {
        if (node == null) {
            return;
        }

        System.out.println("  ".repeat(level) + node.getTagName() + (node.getNthChild() > 1 ? "[" + node.getNthChild() + "]" : ""));
        for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
            System.out.println("  ".repeat(level + 1) + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        for (DomNode child : node.getChildren()) {
            printDomTree(child, level + 1);
        }
    }

    public static void main(String[] args) {
        List<String> xpaths = getAllXPaths();

        DomNode root = DOMTreeBuilder.buildDOMTree(xpaths).getChildren().get(0);


        printDomTree(root, 0);

        if (!root.getChildren().isEmpty()) {
            DomNode exampleNode = root.getChildren().get(0); // Assuming we take the first child for demonstration
            System.out.println("Full XPath: " + exampleNode.getFullXPath());
        }
        driver.quit();
    }
}

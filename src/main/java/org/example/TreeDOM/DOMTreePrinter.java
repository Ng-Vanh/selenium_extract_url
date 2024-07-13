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
        // Example list of XPath expressions
        List<String> xpaths = getAllXPaths();

        // Build the DOM tree
        DomNode root = DOMTreeBuilder.buildDOMTree(xpaths).getChildren().get(0);


        // Print the DOM tree
        printDomTree(root, 0);

        // Get full XPath of a node
        if (!root.getChildren().isEmpty()) {
            DomNode exampleNode = root.getChildren().get(0); // Assuming we take the first child for demonstration
            System.out.println("Full XPath: " + exampleNode.getFullXPath());
        }
        driver.quit();
    }
}

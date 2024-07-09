package org.example.TreeDOM;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.example.XPathCollector.XPathCollector.getAllXPaths;

public class DOMTreePrinter {
    public static void printDomTree(DomNode node, int level) {
        if (node == null) {
            return;
        }

        System.out.println("  ".repeat(level) + node.getTagName() + "[" + node.getNthChild() + "]");
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
        DomNode root = DOMTreeBuilder.buildDOMTree(xpaths);

        // Print the DOM tree
        printDomTree(root, 0);
    }
}

package org.example.TreeDOM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DomNode {
    private String tagName;
    private Map<String, String> attributes;
    private List<DomNode> children;
    private int nthChild;
    private DomNode parent;

    public DomNode(String tagName, Map<String, String> attributes, int nthChild) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.children = new ArrayList<>();
        this.nthChild = nthChild;
        this.parent = null;
    }

    public String getTagName() {
        return tagName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public List<DomNode> getChildren() {
        return children;
    }

    public int getNthChild() {
        return nthChild;
    }

    public DomNode getParent() {
        return parent;
    }

    public void setParent(DomNode parent) {
        this.parent = parent;
    }

    public void addChild(DomNode child) {
        child.setParent(this);
        this.children.add(child);
    }

    public String getFullXPath() {
        StringBuilder xpath = new StringBuilder();
        DomNode current = this;
        while (current != null) {
            String tagName = current.getTagName();

            int nthChild = current.getNthChild();

            if (nthChild > 1 || (current.getParent() != null && current.getParent().getChildren().size() > 1)) {
                xpath.insert(0, "/" + tagName + "[" + nthChild + "]");
            } else {
                xpath.insert(0, "/" + tagName);
            }

            current = current.getParent();
        }
        return xpath.toString().substring(1);
    }

    @Override
    public String toString() {
        return "DomNode{" +
                "tagName='" + tagName + '\'' +
                ", attributes=" + attributes +
                ", children=" + children +
                ", nthChild=" + nthChild +
                '}';
    }
}

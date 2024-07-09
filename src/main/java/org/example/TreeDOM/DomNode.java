package org.example.TreeDOM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DomNode {
    private String tagName;
    private Map<String, String> attributes;
    private List<DomNode> children;
    private int nthChild;

    public DomNode(String tagName, Map<String, String> attributes, int nthChild) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.children = new ArrayList<>();
        this.nthChild = nthChild;
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

    public void addChild(DomNode child) {
        this.children.add(child);
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

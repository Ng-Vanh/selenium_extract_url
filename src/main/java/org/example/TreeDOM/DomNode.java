package org.example.TreeDOM;

import org.openqa.selenium.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DomNode {
    private String tagName;
    private String id;
    private Map<String, String> attributes;
    private List<DomNode> children;
    private int nthChild;
    private DomNode parent;
    private Point absolutePosition;
    private Point relativePosition;

    public DomNode(String tagName, Map<String, String> attributes, int nthChild) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.children = new ArrayList<>();
        this.nthChild = nthChild;
        this.parent = null;
    }
    public DomNode(String tagName, Map<String, String> attributes, int nthChild, Point absolutePosition) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.children = new ArrayList<>();
        this.nthChild = nthChild;
        this.parent = null;
        this.absolutePosition = absolutePosition;
        this.relativePosition = null;
    }

    public DomNode(String tagName,String id, Map<String, String> attributes, int nthChild, Point absolutePosition) {
        this.tagName = tagName;
        this.id = id;
        this.attributes = attributes;
        this.children = new ArrayList<>();
        this.nthChild = nthChild;
        this.parent = null;
        this.absolutePosition = absolutePosition;
        this.relativePosition = null;
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

    public Point getAbsolutePosition() {
        return absolutePosition;
    }

    public Point getRelativePosition() {
        if (relativePosition == null && parent != null) {
            Point parentPosition = parent.getAbsolutePosition();
            relativePosition = new Point(absolutePosition.getX() - parentPosition.getX(), absolutePosition.getY() - parentPosition.getY());
        }
        return relativePosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return xpath.toString().substring(5);
    }
    @Override
    public String toString() {
        return "DomNode{" +
                "tagName='" + tagName + '\'' +
                ", id='" + id + '\'' +
                ", attributes=" + attributes +
                ", children=" + children +
                ", nthChild=" + nthChild +
                ", absolutePosition=" + absolutePosition +
                '}';
    }
}

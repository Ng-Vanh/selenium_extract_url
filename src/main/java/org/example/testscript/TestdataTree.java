package org.example.testscript;

import org.example.testscript.obj.AbstractNode;

import java.util.List;

/**
 * Graph dai dien cho mot test data
 */
public class TestdataTree {
    private   List<AbstractNode> vertices; // danh sach dinh

    public List<AbstractNode> getVertices() {
        return vertices;
    }

    public void setVertices(List<AbstractNode> vertices) {
        this.vertices = vertices;
    }
}
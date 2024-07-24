package org.example.testscript;

import org.example.testscript.obj.AbstractNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.List;

public class TestdataAnalysis {
    private TestdataNLP testdataNLP;
    private POSTagging tagging;
//    public TestdataTree analyzeTestdata() {
//        TestdataTree testdataTree = new TestdataTree();
//        List<String> steps = new ArrayList<>();
//
//        try {
//            FileReader reader = new FileReader(this.testdataNLP.getSourcePath());
//            BufferedReader read = new BufferedReader(reader);
//            String line;
//            while((line = read.readLine()) != null) {
//                steps.add(line);
//            }
//            this.testdataNLP.setSteps(steps);
//            List<AbstractNode> vertices = new ArrayList<>();
//            for(String step : this.testdataNLP.getSteps()) {
//                System.out.println("Step: " + step);
//                tagging = new POSTagging(step);
//                AbstractNode node = tagging.tagging();
//                if(node != null) {
//                    vertices.add(node);
//                }
//            }
//            testdataTree.setVertices(vertices);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//        return testdataTree;
//    }

    public TestdataTree analyzeTestdata() {
        TestdataTree testdataTree = new TestdataTree();
        List<String> steps = new ArrayList<>();
        String currentUrl = null;

        try {
            FileReader reader = new FileReader(this.testdataNLP.getSourcePath());
            BufferedReader read = new BufferedReader(reader);
            String line;
            while ((line = read.readLine()) != null) {
                steps.add(line);
            }
            this.testdataNLP.setSteps(steps); // Add steps to list
            List<AbstractNode> vertices = new ArrayList<>();
            for (String step : this.testdataNLP.getSteps()) {
                System.out.println("Step: " + step);
                if (step.toLowerCase().startsWith("open")) {
                    currentUrl = step.split(" ")[1]; // Get URL from "Open" step
                }
                tagging = new POSTagging(step, currentUrl);
                AbstractNode node = POSTagging.tagging();
                if (node != null) {
                    vertices.add(node);
                }
            }
            testdataTree.setVertices(vertices);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testdataTree;
    }

    public void setTestdataNLP(TestdataNLP testdataNLP) {
        this.testdataNLP = testdataNLP;
    }

    public TestdataNLP getTestdataNLP() {
        return testdataNLP;
    }
    public void convertToSeleniumCode(TestdataTree testdataTree) {
        List<AbstractNode> nodes = testdataTree.getVertices();
        for (AbstractNode node : nodes) {
            System.out.println(node.toSeleniumCode());
        }
    }
    public static void main(String[] args) {
        TestdataAnalysis analysis = new TestdataAnalysis();
        TestdataNLP nlp = new TestdataNLP();
        nlp.setSourcePath(new File("src/main/resources/data/io"));
        analysis.setTestdataNLP(nlp);
        TestdataTree tree = analysis.analyzeTestdata();
        analysis.convertToSeleniumCode(tree);
    }
}

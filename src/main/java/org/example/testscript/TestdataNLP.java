package org.example.testscript;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestdataNLP {
    private File sourcePath; // luu o tep nao

    private List<String> steps; // moi String dai dien cho mot step

    public TestdataNLP() {
        steps = new ArrayList<>();
    }
    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public void setSourcePath(File sourcePath) {
        this.sourcePath = sourcePath;
    }

    public File getSourcePath() {
        return sourcePath;
    }
}

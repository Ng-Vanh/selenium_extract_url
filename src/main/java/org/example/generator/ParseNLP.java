package org.example.generator;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import org.example.TreeDOM.DOMTreeBuilder;
import org.example.TreeDOM.DomNode;
import org.openqa.selenium.WebElement;

import static org.example.TreeDOM.DOMTreeBuilder.buildDOMTree;
import static org.example.TreeDOM.DOMTreePrinter.printDomTree;

public class ParseNLP {
    public static String modelPath = "edu/stanford/nlp/models/pos-tagger/english-left3words-distsim.tagger";
    private static StanfordCoreNLP pipeline;
    public static String FILE_DATA = "src/main/resources/data/descript.xlsx";
    public static int NUMBER_COL = 4;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");

        File modelFile = new File(modelPath);
        if (!modelFile.exists()) {
            System.err.println("Model file not found: " + modelPath);
        }

        props.setProperty("pos.model", modelPath);

        pipeline = new StanfordCoreNLP(props);
    }

    public static List<String> getInputData(String src, int col) {
        List<String> res = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(src);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Đọc tất cả các sheet
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                for (Row row : sheet) {
                    StringBuilder tmp = new StringBuilder();
                    Cell cell = row.getCell(col);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                tmp.append(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    tmp.append(sdf.format(cell.getDateCellValue()));
                                } else {
                                    tmp.append(cell.getNumericCellValue());
                                }
                                break;
                            case BOOLEAN:
                                tmp.append(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                switch (cell.getCachedFormulaResultType()) {
                                    case NUMERIC:
                                        tmp.append(cell.getNumericCellValue());
                                        break;
                                    case STRING:
                                        tmp.append(cell.getStringCellValue());
                                        break;
                                    default:
                                        tmp.append(cell.getCellFormula());
                                }
                                break;
                            default:
                                System.out.println("Cell type not supported.");
                                System.out.println(cell.getCellType());
                        }
                    } else {
                        tmp.append("");
                    }
                    res.add(tmp.toString());
                }
            }
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void generateScript(List<Step> steps){
        String urlStep = null;
        DomNode rootTree = null;
        for(Step step:steps){
            if(step.getAction().equals("Open")){
                String url = step.getObject();
                urlStep = url;
                System.out.println("driver.get(\"" +urlStep + "\")");
                rootTree = DOMTreeBuilder.buildDOMTree(url);
            }
            if(step.getAction().equals("Fill")){
                System.out.println("In ra Fill:");
                List<String> cond = new ArrayList<>();
                cond.add(step.getField());
                cond.add("input");
                cond.add("textarea");
                List<DomNode> sampleNode = DOMTreeBuilder.findNodeCondition(cond, rootTree);
                System.out.println("Soluong: "+ sampleNode.size());
                for(DomNode node: sampleNode){
                    System.out.println(node.getTagName() + "----" + node.getFullXPath() );
                    System.out.println("     " +node.getAttributes().keySet());
                    System.out.println("     " +node.getAttributes().values());
                    System.out.println();

                }

            }
            if(step.getAction().equals("Click")){
//                System.out.println("In ra button");
//                List<String> cond = new ArrayList<>();
//                cond.add(step.getField());
//                List<DomNode> sampleNode = DOMTreeBuilder.findNodeCondition(cond, rootTree);
//                System.out.println(sampleNode.size());
            }
        }

    }
    public static Step analyzeStep(String step) {
        CoreDocument document = new CoreDocument(step.substring(2));
        pipeline.annotate(document);

        String action = null;
        String obj = null;
        String field = null;

        // Process the entire step as a single sentence
        for (CoreSentence sentence : document.sentences()) {
            String sentenceText = sentence.text().trim();
            System.out.println(sentenceText);

            if (sentenceText.startsWith("Open")) {
                action = "Open";
                obj = sentenceText.substring(5).trim(); // Adjust index to skip "Open "
            } else if (sentenceText.startsWith("Fill")) {
                action = "Fill";
                Pattern pattern = Pattern.compile("\"([^\"]*)\"");
                Matcher matcher = pattern.matcher(sentenceText);
                List<String> matches = new ArrayList<>();
                while (matcher.find()) {
                    matches.add(matcher.group(1));
                }
                if (matches.size() >= 2) {
                    obj = matches.get(0); // Input text
                    field = matches.get(1); // Field name
                } else {
                    obj = "No object found";
                    field = "No field found";
                }
            } else if (sentenceText.startsWith("Click")) {
                action = "Click";
                obj = sentenceText.substring(6).trim();
            }

            // Print results for each sentence

            return new Step(action,obj,field);
        }
        return null;
    }

    public static void main(String[] args) {
        List<String> data = getInputData(FILE_DATA, NUMBER_COL);
        data.remove(0);
        List<Step> st = new ArrayList<>();
        for (String s: data.get(0).split("\n")){
            st.add(analyzeStep(s));
        }


        generateScript(st);
    }
}

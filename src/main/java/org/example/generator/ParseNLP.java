package org.example.generator;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.TreeDOM.DOMTreeBuilder;
import org.example.TreeDOM.DomNode;
import org.openqa.selenium.By;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.TreeDOM.DOMTreeBuilder.buildDOMTree;
import static org.example.XPathCollector.XPathCollector.driver;

public class ParseNLP {
    public static final String MODEL_PATH = "edu/stanford/nlp/models/pos-tagger/english-left3words-distsim.tagger";
    private static final StanfordCoreNLP PIPELINE;
    public static final String FILE_DATA = "src/main/resources/data/descript.xlsx";
    public static final int NUMBER_COL = 4;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");
        props.setProperty("pos.model", MODEL_PATH);

        File modelFile = new File(MODEL_PATH);
        if (!modelFile.exists()) {
            System.err.println("Model file not found: " + MODEL_PATH);
        }

        PIPELINE = new StanfordCoreNLP(props);
    }

    public static List<String> getInputData(String src, int col) {
        List<String> res = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(src);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                for (Row row : sheet) {
                    Cell cell = row.getCell(col);
                    res.add(cell != null ? getCellValue(cell) : "");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return getFormulaCellValue(cell);
            default:
                return "";
        }
    }

    private static String getFormulaCellValue(Cell cell) {
        switch (cell.getCachedFormulaResultType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            default:
                return cell.getCellFormula();
        }
    }

    public static void generateScript(List<Step> steps) {
        String urlStep = null;
        DomNode rootTree = null;
        boolean loggedIn = false;
        for (Step step : steps) {
            switch (step.getAction()) {
                case "Open":
                    urlStep = step.getObject();
                    System.out.println("===============TestScript open link=============");
                    System.out.println("driver.get(\"" + urlStep + "\")");
                    System.out.println("================================================");
                    rootTree = buildDOMTree(urlStep);
                    break;
                case "Fill":
                    processFillStep(step, rootTree);
                    break;
                case "Click":
                    processClickStep(step, rootTree);
                    break;
                case "Hover":
//                    if (!loggedIn) {
//                        loginToSystem();
//                        loggedIn = true;
//                        rootTree = buildDOMTree(urlStep);
//                        processHoverStep(step,rootTree);
//                    }
//                    if (step.getAction().equals("Click")) {
//                        processClickStep(step, rootTree);
//                    } else {
//                        processHoverStep(step, rootTree);
//                    }
                    break;
            }
        }
    }

    private static void loginToSystem() {
        String loginUrl = "https://webtest.ranorex.org/wp-admin/";
        System.out.println("===============TestScript open link=============");
        System.out.println("driver.get(\"" + loginUrl + "\")");
        System.out.println("================================================");
        driver.get(loginUrl);
        driver.findElement(By.id("user_login")).sendKeys("ranorex webtest");
        driver.findElement(By.id("user_pass")).sendKeys("ranorex");
        driver.findElement(By.id("wp-submit")).click();
    }
    private static void processFillStep(Step step, DomNode rootTree) {
        List<String> cond = Arrays.asList(step.getField(),"input");
        DomNode node = DOMTreeBuilder.findNodeCondition(cond, rootTree);

        if (node != null) {
            String id = node.getAttributes().get("id");
            String name = node.getAttributes().get("name");
            String cssSelector = node.getAttributes().get("class");
            String xpath = node.getFullXPath();

            if (id != null && !id.isEmpty()) {
                System.out.println("===============TestScript By Id=============");
                System.out.println("driver.findElement(By.id(\"" + id + "\")).sendKeys(\"" + step.getObject() + "\")");
            } else if (name != null && !name.isEmpty()) {
                System.out.println("===============TestScript By Name=============");
                System.out.println("driver.findElement(By.name(\"" + name + "\")).sendKeys(\"" + step.getObject() + "\")");
            } else if (cssSelector != null && !cssSelector.isEmpty()) {
                System.out.println("===============TestScript By CssSelector=============");
                System.out.println("driver.findElement(By.cssSelector(\"" + cssSelector + "\")).sendKeys(\"" + step.getObject() + "\")");
            } else if (xpath != null && !xpath.isEmpty()) {
                System.out.println("===============TestScript By XPath=============");
                System.out.println("driver.findElement(By.xpath(\"" + xpath + "\")).sendKeys(\"" + step.getObject() + "\")");
            } else {
                System.out.println("=========================TEST===============");
            }
            System.out.println("============================================");
        } else {
            System.out.println("No matching node found.");
        }
    }

    private static void processClickStep(Step step, DomNode rootTree) {
        List<String> cond = Arrays.asList(step.getField(), step.getObject());
        DomNode node = DOMTreeBuilder.findNodeCondition(cond, rootTree);
        if (node != null) {
            String id = node.getAttributes().get("id");
            String name = node.getAttributes().get("name");
            String cssSelector = node.getAttributes().get("class");
            String xpath = node.getFullXPath();

            if (id != null && !id.isEmpty()) {
                System.out.println("===============TestScript By Id=============");
                System.out.println("driver.findElement(By.id(\"" + id + "\")).click()");
            } else if (name != null && !name.isEmpty()) {
                System.out.println("===============TestScript By Name=============");
                System.out.println("driver.findElement(By.name(\"" + name + "\")).click()");
            } else if (cssSelector != null && !cssSelector.isEmpty()) {
                System.out.println("===============TestScript By CssSelector=============");
                System.out.println("driver.findElement(By.cssSelector(\"" + cssSelector + "\")).click()");
            } else if (xpath != null && !xpath.isEmpty()) {
                System.out.println("===============TestScript By XPath=============");
                System.out.println("driver.findElement(By.xpath(\"" + xpath + "\")).click()");
            } else {
                System.out.println("=========================TEST===============");
            }
            System.out.println("============================================");
        } else {
            System.out.println("No matching node found.");
        }
    }

    private static void processHoverStep(Step step, DomNode rootTree) {
        System.out.println("In ra Hover:");
        List<String> cond = Arrays.asList(step.getField(), step.getObject());
        DomNode node = DOMTreeBuilder.findNodeCondition(cond, rootTree);

        if (node != null) {
            String id = node.getAttributes().get("id");
            String name = node.getAttributes().get("name");
            String cssSelector = node.getAttributes().get("class");
            String xpath = node.getFullXPath();

            if (id != null && !id.isEmpty()) {
                System.out.println("===============TestScript By Id=============");
                System.out.println("driver.findElement(By.id(\"" + id + "\"));");
            } else if (name != null && !name.isEmpty()) {
                System.out.println("===============TestScript By Name=============");
                System.out.println("driver.findElement(By.name(\"" + name + "\"));");
            } else if (cssSelector != null && !cssSelector.isEmpty()) {
                System.out.println("===============TestScript By CssSelector=============");
                System.out.println("driver.findElement(By.cssSelector(\"" + cssSelector + "\"));");
            } else if (xpath != null && !xpath.isEmpty()) {
                System.out.println("===============TestScript By XPath=============");
                System.out.println("driver.findElement(By.xpath(\"" + xpath + "\"));");
            } else {
                System.out.println("=========================TEST===============");
            }
            System.out.println("Actions actions = new Actions(driver);");
            System.out.println("actions.moveToElement(element).perform();");
            System.out.println("============================================");
        } else {
            System.out.println("No matching node found.");
        }
    }

    public static Step analyzeStep(String step) {
        CoreDocument document = new CoreDocument(step.substring(2));
        PIPELINE.annotate(document);

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
                if (matches.size() >= 1) {
                    obj = matches.get(0); // Button name or identifier
                    field = matches.size() > 1 ? matches.get(1) : "No field found"; // Optional field name, if any
                } else {
                    obj = "No object found";
                    field = "No field found";
                }
            } else if (sentenceText.startsWith("Click")) {
                action = "Click";
                Pattern pattern = Pattern.compile("\"([^\"]*)\"");
                Matcher matcher = pattern.matcher(sentenceText);
                List<String> matches = new ArrayList<>();

                // Tìm tất cả các chuỗi trong ngoặc kép
                while (matcher.find()) {
                    matches.add(matcher.group(1));
                }

                if (matches.size() >= 1) {
                    obj = matches.get(0); // Lấy đối tượng chính, như là tên nút "Login"

                    // Tìm từ sau dấu ngoặc kép để lấy field
                    int endQuoteIndex = sentenceText.indexOf("\"", sentenceText.indexOf(matches.get(0)) + 1);
                    if (endQuoteIndex != -1 && endQuoteIndex + 1 < sentenceText.length()) {
                        String remainingText = sentenceText.substring(endQuoteIndex + 1).trim();
                        // Field là từ đầu tiên trong phần còn lại của câu
                        field = remainingText.split("\\s+")[0];
                    } else {
                        field = "No field found";
                    }
                } else {
                    obj = "No object found";
                    field = "No field found";
                }
            } else if (sentenceText.startsWith("Hover")) {
                action = "Hover";
                List<String> matches = extractQuotedStrings(sentenceText);
                if (matches.size() >= 1) {
                    obj = matches.get(0); // Element to hover over
                    field = matches.size() > 1 ? matches.get(1) : "No field found"; // Field name, if any
                } else {
                    obj = "No object found";
                    field = "No field found";
                }
                if (obj.equals("No object found")) {
                    String patternString = "Hover over the ([^,]+)";
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(sentenceText);
                    if (matcher.find()) {
                        obj = matcher.group(1).trim();
                    }
                }
            }

            // Print results for each sentence

            return new Step(action,obj,field);
        }
        return null;
    }

    private static List<String> extractQuotedStrings(String text) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"").matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }

    public static void main(String[] args) {
        List<String> data = getInputData(FILE_DATA, NUMBER_COL);
        data.remove(0);

//        for(String da: data){
//            System.out.println(">>>>>>>>>>Start>>>>>>>>");
//            List<Step> steps = new ArrayList<>();
//            for (String s : da.split("\n")) {
//                steps.add(analyzeStep(s));
//            }
//            generateScript(steps);
//            System.out.println(">>>>>>>>>>End>>>>>>>>");
//
//        }


        List<Step> steps = new ArrayList<>();
        for(String s: data.get(3).split("\n")){
            steps.add(analyzeStep(s));
        }
        generateScript(steps);
        driver.quit();


    }
}

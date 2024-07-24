package org.example.generator;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;

public class ParseNLP {
    public static String modelPath = "edu/stanford/nlp/models/pos-tagger/english-left3words-distsim.tagger";
    private static StanfordCoreNLP pipeline;
    private static final String URL_REGEX = "(https?://\\S+)";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    public static String FILE_DATA = "src/main/resources/data/descript.xlsx";
    public static int NUMBER_COL = 4;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse");
        props.setProperty("pos.model", modelPath);

        pipeline = new StanfordCoreNLP(props);
    }

    public static List<String> getInputData(String src, int col) {
        List<String> res = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(src);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
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
                                tmp.append(cell.getDateCellValue());
                            } else {
                                tmp.append(cell.getNumericCellValue());
                            }
                            break;
                        case BOOLEAN:
                            tmp.append(cell.getBooleanCellValue());
                            break;
                        case FORMULA:
                            tmp.append(cell.getCellFormula());
                            break;
                        default:
                            System.out.println("Cell type not supported.");
                            System.out.println(cell.getCellType());
                    }
                }
                res.add(tmp.toString());
            }
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<TokenInfo> analyzeText(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);

        List<TokenInfo> tokenList = new ArrayList<>();

        // Tìm kiếm URL bằng regex
        Matcher matcher = URL_PATTERN.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            tokenList.add(new TokenInfo(url, "URL"));
        }

        // Lấy các token và POS từ NLP
        for (CoreLabel token : document.tokens()) {
            String word = token.word();
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

            // Nếu từ đã được thêm vào List từ bước tìm URL, không thêm lại
            boolean alreadyAdded = tokenList.stream().anyMatch(ti -> ti.getWord().equals(word));
            if (!alreadyAdded) {
                tokenList.add(new TokenInfo(word, pos));
            }
        }

        return tokenList;
    }
    public static String generateTestScript(String testInput) {
        String[] lines = testInput.split("\n");
        StringBuilder res = new StringBuilder("driver.");

        for (String line : lines) {
            List<TokenInfo> tokenList = analyzeText(line);

            for (TokenInfo tokenInfo : tokenList) {
                String word = tokenInfo.getWord();
                String pos = tokenInfo.getPos();

                if (word.equalsIgnoreCase("Open") && pos.equals("VB")) {
                    res.append("get(");
                }
                else if (pos.equals("URL")) {
                    res.append(word + ")");
                }
            }
        }

        return res.toString();
    }

    public static Map<String, String> parseSVOA(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);

        Map<String, String> svoa = new HashMap<>();

        for (CoreSentence sentence : document.sentences()) {
            SemanticGraph dependencies = sentence.dependencyParse();

            // Find Subject
            IndexedWord subject = dependencies.getFirstRoot();
            svoa.put("Subject", subject.word());

            // Find Verb
            for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
                if (edge.getRelation().toString().equals("nsubj")) {
                    svoa.put("Verb", edge.getDependent().word());
                } else if (edge.getRelation().toString().startsWith("obj")) {
                    svoa.put("Object", edge.getDependent().word());
                } else if (edge.getRelation().toString().startsWith("advmod")) {
                    svoa.put("Adverbial", edge.getDependent().word());
                }
            }
        }

        return svoa;
    }

    public static void main(String[] args) {
        List<String> data = getInputData(FILE_DATA, NUMBER_COL);
        if (data != null && !data.isEmpty()) {
            String testInput = data.get(1);
            String script = generateTestScript(testInput);
            System.out.println(script);
        } else {
            System.out.println("No data found or error reading data.");
        }
    }

}

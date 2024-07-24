package org.example.POSTaggingEx;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;

import java.io.File;
import java.util.Properties;

public class POSTaggingExample {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos");

        String modelPath = "edu/stanford/nlp/models/pos-tagger/english-left3words-distsim.tagger";
        File modelFile = new File(modelPath);
        if (!modelFile.exists()) {
            System.err.println("Model file not found: " + modelPath);
            return;
        }

        props.setProperty("pos.model", modelPath);

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String text = "Open http://webtest.ranorex.org/wp-login.php" +
                "2. Fill ranorex webtest into Username" +
                "3. Fill 123 into Password field " +
                "4. Click Login button";

        CoreDocument document = new CoreDocument(text);

        pipeline.annotate(document);

        for (CoreSentence sentence : document.sentences()) {
            for (CoreLabel token : sentence.tokens()) {
                String word = token.word();
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                System.out.println(word + " : " + pos);
            }
        }
    }
}

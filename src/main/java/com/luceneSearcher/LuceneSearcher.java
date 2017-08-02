package com.luceneSearcher;

import com.luceneSearcher.factory.SpanNearQueryFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Created by Oliver on 8/2/2017.
 */
@RestController
@EnableAutoConfiguration
public class LuceneSearcher {

    private SpanNearQueryFactory spanNearQueryFactory;

    private Directory directory = new SimpleFSDirectory(new File("C:\\Users\\Oliver\\Documents\\NlpTrainingData\\Lucene\\Index"));

    private IndexReader indexReader = DirectoryReader.open(directory);

    public LuceneSearcher() throws IOException {
        final ApplicationContext context = new ClassPathXmlApplicationContext("spring_beans.xml");
        this.spanNearQueryFactory = (SpanNearQueryFactory) context.getBean("spanNearQueryFactory");
        this.directory = new SimpleFSDirectory(new File("C:\\Users\\Oliver\\Documents\\NlpTrainingData\\Lucene\\Index"));
        this.indexReader = DirectoryReader.open(directory);
    }

    @RequestMapping(path = "/search", method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    String process(@RequestParam(value = "terms") String termsAsString) throws IOException, InterruptedException {
        List<String> terms = Arrays.asList(termsAsString.split(" "));
        return processSpanNearQuery(spanNearQueryFactory.create(terms));
    }

    private String processSpanNearQuery(SpanNearQuery nearQuery) throws IOException {
        Map<Term, TermContext> termContexts = new HashMap<>();
        Spans spans = nearQuery.getSpans(indexReader.getContext().leaves().get(0), null, termContexts);
        List<String> foundSentences = processSpans(spans);
        StringBuilder stringBuilder = new StringBuilder();
        for (String sentence : foundSentences) {
            stringBuilder.append(sentence);
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private List<String> processSpans(Spans spans) throws IOException {
        Directory directory = new SimpleFSDirectory(new File("C:\\Users\\Oliver\\Documents\\NlpTrainingData\\Lucene\\Index"));
        IndexReader reader = DirectoryReader.open(directory);
        List<String> result = new ArrayList<String>();
        while (spans.next()) {
            int id = spans.doc();
            Document doc = reader.document(id);
            Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
            TokenStream stream = analyzer.tokenStream("", new StringReader(doc.get("sentence")));
            stream.reset();
            StringBuffer buffer = new StringBuffer();
            int i = 0;
            while (stream.incrementToken()) {
                if (i == spans.start()) {
                    buffer.append("<");
                }
                buffer.append(stream.getAttribute(CharTermAttribute.class).toString());
                if (i + 1 == spans.end()) {
                    buffer.append(">");
                }
                buffer.append(" ");
                i++;
            }
            result.add(buffer.toString());
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LuceneSearcher.class, args);
    }
}

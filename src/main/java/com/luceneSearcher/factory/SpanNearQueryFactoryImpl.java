package com.luceneSearcher.factory;

import com.luceneSearcher.tokens.Tokenizer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import java.util.List;

/**
 * Created by Oliver on 8/2/2017.
 */
public class SpanNearQueryFactoryImpl implements SpanNearQueryFactory {

    private Tokenizer tokenizer;

    public SpanNearQueryFactoryImpl(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public SpanNearQuery create(List<String> tems) {
        SpanQuery[] spanQueries = new SpanQuery[tems.size()];
        for (int i = 0; i < tems.size(); i++) {
            spanQueries[i] = new SpanTermQuery(new Term("sentence", tokenizer.decapitalize(tems.get(i))));
        }
        return new SpanNearQuery(spanQueries, 100, true);
    }
}

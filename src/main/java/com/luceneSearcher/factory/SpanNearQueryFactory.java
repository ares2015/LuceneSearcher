package com.luceneSearcher.factory;

import org.apache.lucene.search.spans.SpanNearQuery;

import java.util.List;

/**
 * Created by Oliver on 8/2/2017.
 */
public interface SpanNearQueryFactory {

    SpanNearQuery create(List<String> tems);

}

package com.luceneSearcher.tokens;

import java.util.List;
import java.util.Set;

/**
 * Created by Oliver on 8/2/2017.
 */
public interface Tokenizer {

    List<String> splitStringIntoList(String sentence);

    String removeCommaAndDot(final String token);

    String decapitalize(String token);

    Set<Integer> getCommaIndexes(List<String> tokens);

}

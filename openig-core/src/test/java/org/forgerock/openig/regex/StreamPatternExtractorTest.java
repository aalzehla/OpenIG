/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2014 ForgeRock AS.
 */

package org.forgerock.openig.regex;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.forgerock.openig.regex.Readers.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

public class StreamPatternExtractorTest {
    @Test
    public void testSimple() throws Exception {
        StreamPatternExtractor extractor = new StreamPatternExtractor();
        extractor.patterns.put("extra-header", Pattern.compile("^X-(.*): "));
        extractor.templates.put("extra-header", new PatternTemplate("Found header '$1'"));

        Map<String, String> actual = asMap(extractor.extract(reader("X-Hello: \"World\"", "Not-Extra: Hi")));
        assertThat(actual)
                .hasSize(1)
                .includes(entry("extra-header", "Found header 'Hello'"));
    }

    @Test
    public void testMultiPatternsMatching() throws Exception {
        StreamPatternExtractor extractor = new StreamPatternExtractor();
        extractor.patterns.put("header", Pattern.compile("(.*): \\\"(.*)\\\""));
        extractor.templates.put("header", new PatternTemplate("$2"));
        extractor.patterns.put("name", Pattern.compile("(.*): "));
        extractor.templates.put("name", new PatternTemplate("$1"));

        Map<String, String> actual = asMap(extractor.extract(reader("X-Hello: \"World\"")));
        assertThat(actual)
                .hasSize(2)
                .includes(
                    entry("header", "World"),
                    entry("name", "X-Hello"));
    }

    public static <K, V> Map<K, V> asMap(Iterable<Map.Entry<K, V>> iterable) {
        Map<K, V> map = new HashMap<K, V>();
        for (Map.Entry<K, V> item : iterable) {
            map.put(item.getKey(), item.getValue());
        }
        return map;
    }
}
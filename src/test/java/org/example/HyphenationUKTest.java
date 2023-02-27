package org.example;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class HyphenationUKTest {
    private SimpleHyphenator hyphenator;

    @Before
    public void before() {
        try {
            hyphenator = new SimpleHyphenator("en");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hyph(String hyph) {
        return hyphenator.hyphenate(hyph);
    }
    @Test
    public void testComplicatedWords() {
        Assert.assertEquals("Hy\u00ADphen\u00ADa\u00ADtion", hyph("Hyphenation"));
        Assert.assertEquals("Lit\u00ADer\u00ADally", hyph("Literally"));
        Assert.assertEquals("Ironic", hyph("Ironic"));
        Assert.assertEquals("Ir\u00ADregard\u00ADless", hyph("Irregardless"));
        Assert.assertEquals("Whom", hyph("Whom"));
        Assert.assertEquals("Col\u00ADonel", hyph("Colonel"));
        Assert.assertEquals("Non\u00ADplussed", hyph("Nonplussed"));
        Assert.assertEquals("Dis\u00ADin\u00ADter\u00ADested", hyph("Disinterested"));
        Assert.assertEquals("Enorm\u00ADity", hyph("Enormity"));
        Assert.assertEquals("Lieu\u00ADten\u00ADant", hyph("Lieutenant"));
        Assert.assertEquals("Un\u00ADabashed", hyph("Unabashed"));

        Assert.assertEquals("between", hyph("between"));
        Assert.assertEquals("beowulf", hyph("beowulf"));
        Assert.assertEquals("aca\u00ADdemic", hyph("academic"));

        Assert.assertEquals("something", hyph("something"));

        Assert.assertEquals(
                "Some\u00ADtimes to un\u00ADder\u00ADstand a word's mean\u00ADing you need more than a defin\u00ADi\u00ADtion; you need to see the word used in a sen\u00ADtence.",
                hyph("Sometimes to understand a word's meaning you need more than a definition; you need to see the word used in a sentence.")
        );
    }
}

package com.jsoft.magenta.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FormatterTest
{
    @Test
    @DisplayName("Capitalize first letter")
    public void capitalizeFirstLetter()
    {
        String word = "toCapitalize";
        word = WordFormatter.capitalize(word);
        Assertions.assertEquals(word.charAt(0), 'T');
    }

    @Test
    @DisplayName("Capitalize and format other letters in word")
    public void capitalizeAndFormat()
    {
        String word = "toCapitalize";
        word = WordFormatter.capitalizeFormat(word);
        Assertions.assertEquals(word.charAt(0), 'T');
        boolean assertion = true;
        for(int i=1; i<word.length(); i++)
            if(Character.isUpperCase(word.charAt(i)))
                assertion = false;

        Assertions.assertTrue(assertion);
    }

    @Test
    @DisplayName("Capitalize, format and trim multiple words in a single string")
    public void capitalizeAndFormatFully()
    {
        String word = "   MultiPle  worDs    String      ";
        word = WordFormatter.capitalizeFullyFormat(word);
        Assertions.assertNotNull(word.charAt(0));
        Assertions.assertNotNull(word.charAt(word.length()-1));
        Assertions.assertEquals(word.charAt(0), 'M');
        Assertions.assertEquals(word.charAt(12), 'd');
    }

}

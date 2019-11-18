package com.fillumina.xmi2jdl.parser;

import com.fillumina.xmi2jdl.parser.CommentParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author fra
 */
public class CommentParserTest {
    
    @Test
    public void shouldParseNull() {
        CommentParser parser = new CommentParser(null);
        
        assertNull(parser.getComment());
        assertNull(parser.getValidation());
    }
    
    @Test
    public void shouldParseEmptyString() {
        CommentParser parser = new CommentParser("");
        
        assertEquals("", parser.getComment());
        assertNull(parser.getValidation());
    }
    
    @Test
    public void shouldParsePureComment() {
        final String comment = "this is a comment";
        CommentParser parser = new CommentParser(comment);
        
        assertEquals(comment, parser.getComment());
        assertNull(parser.getValidation());
    }
    
    @Test
    public void shouldParsePureValidation() {
        final String validation = "this is a validation";
        CommentParser parser = new CommentParser("{" + validation + "}");
        
        assertNull(parser.getComment());
        assertEquals(validation, parser.getValidation());
    }
    
    @Test
    public void shouldParseCommentWithValidation() {
        CommentParser parser = new CommentParser(
                "this is a {validated1 validated2} comment");
        
        assertEquals("this is a comment", parser.getComment());
        assertEquals("validated1 validated2", parser.getValidation());
    }
    
    @Test
    public void shouldNotParseCommentWithTwoValidations() {
        
        assertThrows(RuntimeException.class, () -> 
            new CommentParser("this is a {validated1} {validated2} comment"));
    }
}

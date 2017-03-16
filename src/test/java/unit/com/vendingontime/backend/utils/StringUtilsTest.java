package unit.com.vendingontime.backend.utils;

import com.vendingontime.backend.utils.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alberto on 16/3/17.
 */
public class StringUtilsTest {

    @Test
    public void isEmpty_empty_true() throws Exception {
        assertTrue(StringUtils.isEmpty(""));
    }

    @Test
    public void isEmpty_null_true() throws Exception {
        assertTrue(StringUtils.isEmpty(null));
    }

    @Test
    public void isEmpty_nonempty_false() throws Exception {
        assertFalse(StringUtils.isEmpty("a"));
    }

    @Test
    public void isEmpty_blankSpaces_true() throws Exception {
        assertTrue(StringUtils.isEmpty(" "));
    }

    @Test
    public void isEmail_null_false() throws Exception {
        assertFalse(StringUtils.isEmail(null));
    }

    @Test
    public void isEmail_empty_false() throws Exception {
        assertFalse(StringUtils.isEmail(""));
    }

    @Test
    public void isEmail_blankSpaces_false() throws Exception {
        assertFalse(StringUtils.isEmail(" "));
    }

    @Test
    public void isEmail_word_false() throws Exception {
        assertFalse(StringUtils.isEmail("user"));
    }

    @Test
    public void isEmail_wordAtWord_false() throws Exception {
        assertFalse(StringUtils.isEmail("user@example"));
    }

    @Test
    public void isEmail_wordAtWordDot_false() throws Exception {
        assertFalse(StringUtils.isEmail("user@example."));
    }

    @Test
    public void isEmail_wordAtWordDotChar_false() throws Exception {
        assertFalse(StringUtils.isEmail("user@example.c"));
    }

    @Test
    public void isEmail_wordAtWordDotWord_true() throws Exception {
        assertTrue(StringUtils.isEmail("user@example.co"));
    }

    @Test
    public void isEmail_wordDotWordAtWordDotWord_true() throws Exception {
        assertTrue(StringUtils.isEmail("user.name@example.co"));
    }
}
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

    @Test
    public void isDni_null_false() throws Exception {
        assertFalse(StringUtils.isDni(null));
    }

    @Test
    public void isDni_empty_false() throws Exception {
        assertFalse(StringUtils.isDni(""));
    }

    @Test
    public void isDni_blankSpaces_false() throws Exception {
        assertFalse(StringUtils.isDni(" "));
    }

    @Test
    public void isDni_word_false() throws Exception {
        assertFalse(StringUtils.isDni("user"));
    }

    @Test
    public void isDni_number_false() throws Exception {
        assertFalse(StringUtils.isDni("98998"));
    }

    @Test
    public void isDni_short_false() throws Exception {
        assertFalse(StringUtils.isDni("98998X"));
    }

    @Test
    public void isDni_long_true() throws Exception {
        assertFalse(StringUtils.isDni("999999999Z"));
    }

    @Test
    public void isDni_legal_true() throws Exception {
        assertTrue(StringUtils.isDni("99999999Z"));
    }

    @Test
    public void isDni_foreign_true() throws Exception {
        assertTrue(StringUtils.isDni("X88888888W"));
    }

    @Test
    public void isValidUsername_null_false() throws Exception {
        assertFalse(StringUtils.isValidUsername(null));
    }

    @Test
    public void isValidUsername_empty_false() throws Exception {
        assertFalse(StringUtils.isValidUsername(""));
    }

    @Test
    public void isValidUsername_valid_true() throws Exception {
        assertTrue(StringUtils.isValidUsername("user1"));
    }

    @Test
    public void isValidUsername_invalid_false() throws Exception {
        assertFalse(StringUtils.isValidUsername("user1#"));
    }

    @Test
    public void isShort_null_true() throws Exception {
        assertTrue(StringUtils.isShort(null, 1));
    }

    @Test
    public void isShort_nullAnd0_true() throws Exception {
        assertTrue(StringUtils.isShort(null, 0));
    }

    @Test
    public void isShort_emptyAnd0_false() throws Exception {
        assertFalse(StringUtils.isShort("", 0));
    }

    @Test
    public void isShort_long_false() throws Exception {
        assertFalse(StringUtils.isShort("a", 0));
    }

    @Test
    public void isLong_null_false() throws Exception {
        assertFalse(StringUtils.isLong(null, 0));
    }

    @Test
    public void isLong_emptyAnd0_false() throws Exception {
        assertFalse(StringUtils.isLong("", 0));
    }

    @Test
    public void isLong_long_true() throws Exception {
        assertTrue(StringUtils.isLong("aa", 1));
    }

    @Test
    public void isLong_limit_false() throws Exception {
        assertFalse(StringUtils.isLong("a", 1));
    }

    @Test
    public void isLong_short_false() throws Exception {
        assertFalse(StringUtils.isLong("a", 2));
    }

    @Test
    public void createFilled() {
        int length = 5;
        String filled = StringUtils.createFilled(length);
        assertEquals(length, filled.length());
        assertFalse(StringUtils.isEmpty(filled));
    }
}
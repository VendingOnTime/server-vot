package com.vendingontime.backend;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by miguel on 6/3/17.
 */
public class TestClassTest {
    @Test
    public void doSum() throws Exception {
        TestClass tc = new TestClass();

        assertEquals(5, tc.doSum(3, 2));
    }

}
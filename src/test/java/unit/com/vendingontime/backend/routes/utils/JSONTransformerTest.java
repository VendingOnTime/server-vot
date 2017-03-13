package unit.com.vendingontime.backend.routes.utils;

import com.vendingontime.backend.routes.utils.JSONTransformer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Alberto on 13/03/2017.
 */
public class JSONTransformerTest {
    @Test
    public void render() throws Exception {
        String s = new JSONTransformer().render(new String[]{"TEST"});
        assertEquals("[\"TEST\"]", s);
    }

}
package org.lasantha.jetty;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LKDefaultHandlerTest {

    @Test
    void getResourceBytes() {
        final byte[] bytes = LKDefaultHandler.getResourceBytes("favicon.ico");
        assertNotNull(bytes);
        assertEquals(15406, bytes.length);

        assertNull(LKDefaultHandler.getResourceBytes("fooBar.xyz"));
    }
}

package org.lasantha.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LKDefaultHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LKDefaultHandler.class);

    private final byte[] favIconBytes = getResourceBytes("favicon.ico");

    private final String favIconETag = UUID.randomUUID().toString().substring(0, 8);

    @Override
    public void handle(final String target,
                       final Request baseReq,
                       final HttpServletRequest req,
                       final HttpServletResponse res) {
        final long startTime = System.currentTimeMillis();
        try {
            try (final ServletOutputStream os = res.getOutputStream()) {
                if (target.startsWith("/favicon.ico") && favIconBytes != null) {
                    res.setContentType("image/x-icon");
                    res.setHeader("Content-Length", String.valueOf(favIconBytes.length));
                    res.setHeader("ETag", favIconETag);
                    res.setHeader("Cache-Control", "private, max-age=86400");
                    os.write(favIconBytes);
                    res.setStatus(HttpServletResponse.SC_OK);
                } else {
                    res.setContentType(MimeTypes.Type.TEXT_PLAIN_UTF_8.asString());
                    os.println(String.format("Target url not found: %s", target));
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            res.setContentType(MimeTypes.Type.TEXT_PLAIN_UTF_8.asString());
            try (final ServletOutputStream os = res.getOutputStream()) {
                os.println("Unexpected server error");
            } catch (IOException ignored) {
            }
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        final long elapsed = System.currentTimeMillis() - startTime;
        LOG.info("{}", JettyUtils.toLog(elapsed, req.getMethod(), target, res.getStatus()));

        baseReq.setHandled(true);
    }

    static byte[] getResourceBytes(final String resourceName) {
        int size = 0;
        try (final InputStream is = ClassLoader.getSystemResourceAsStream(resourceName)) {
            while (is.read() != -1) {
                size++;
            }
        } catch (Exception e) {
            LOG.error("Unable to find resource {}", resourceName, e);
            return null;
        }

        try (final InputStream is = ClassLoader.getSystemResourceAsStream(resourceName)) {
            final byte[] result = new byte[size];
            int i = 0;
            int byteRead;
            while ((byteRead = is.read()) != -1) {
                result[i++] = (byte) byteRead;
            }
            return result;
        } catch (Exception e) {
            LOG.error("Unable to find resource {}", resourceName, e);
        }

        return null;
    }

}

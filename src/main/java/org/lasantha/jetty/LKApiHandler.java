package org.lasantha.jetty;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LKApiHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LKApiHandler.class);

    private final LKApiMapper mapper = new LKApiMapper();

    @Override
    public void handle(final String target,
                       final Request baseReq,
                       final HttpServletRequest req,
                       final HttpServletResponse res) {
        final long startTime = System.currentTimeMillis();
        res.setContentType(mapper.getContentType());
        try {
            try {
                mapper.map(target, req, res);
                res.setStatus(HttpServletResponse.SC_OK);
            } catch (LKHttpMapperException e) {
                res.getWriter().println(e.getMessage());
                res.setStatus(e.getStatusCode());
            }
        } catch (Exception e) {
            try {
                res.getWriter().println(LKApiMapper.errorJson("Unexpected server error", "AAA000"));
            } catch (IOException ignored) {
            }
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        final long elapsed = System.currentTimeMillis() - startTime;
        LOG.info("{}", JettyUtils.toLog(elapsed, req.getMethod(), target, res.getStatus()));

        baseReq.setHandled(true);
    }

}

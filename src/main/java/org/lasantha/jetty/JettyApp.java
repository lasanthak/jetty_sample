package org.lasantha.jetty;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.jetty.http.HttpMethod.GET;
import static org.eclipse.jetty.http.HttpMethod.POST;
import static org.eclipse.jetty.http.HttpMethod.PUT;

public class JettyApp {

    private static final Logger LOG = LoggerFactory.getLogger(JettyApp.class);

    public static void main(String[] args) throws Exception {
        final Server server = createServer();

        server.start();
        server.join();
    }

    static Server createServer() {
        final int port = 8080;
        final int timeout = 10000;
        LOG.info("Jetty server starting on port {} ...", port);

        final Server server = new Server();

        final HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setIdleTimeout(timeout);
        httpConfig.setOutputBufferSize(32768);
        httpConfig.setRequestHeaderSize(8192);
        httpConfig.setResponseHeaderSize(8192);
        httpConfig.setSendServerVersion(false);
        httpConfig.setSendDateHeader(true);
        final ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        http.setPort(port);
        http.setName("0.0.0.0"); // all interfaces
        http.setIdleTimeout(timeout);
        server.addConnector(http);

        final ContextHandler lkContext = new ContextHandler("/lk");
        lkContext.setHandler(new LKApiHandler());
        final ContextHandler rootContext = new ContextHandler("/");
        rootContext.setHandler(new LKDefaultHandler());
        final ContextHandlerCollection contextHandlers = new ContextHandlerCollection(lkContext, rootContext);

        final GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(contextHandlers);
        gzipHandler.setIncludedMethods(GET.asString(), POST.asString(), PUT.asString());
        gzipHandler.setExcludedMimeTypes(); // by default several types are excluded
        server.setHandler(gzipHandler);

        server.setDumpAfterStart(true);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        createShutdownHook(server);

        return server;
    }

    private static void createShutdownHook(final Server server) {
        Runtime.getRuntime().addShutdownHook(new Thread("LKShutdownHook") {

            @Override
            public void run() {
                try {
                    LOG.info("Shutting down gracefully...");
                } catch (Exception ignored) {
                }
            }
        });
    }
}

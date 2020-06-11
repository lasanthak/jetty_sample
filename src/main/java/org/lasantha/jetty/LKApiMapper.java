package org.lasantha.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LKApiMapper {

    public String getContentType() {
        return MimeTypes.Type.APPLICATION_JSON.asString();
    }

    public void map(final String target,
                    final HttpServletRequest req,
                    final HttpServletResponse res) {
        try {
            final String subTarget = JettyUtils.extractSubTarget(target.substring(1)); // remove leading /
            switch (subTarget) {
                case "hello":
                    hello(req, res.getWriter());
                    break;
                case "world":
                    world(req, res.getWriter());
                    break;
                case "echo":
                    echo(req, res.getWriter());
                    break;
                default:
                    throw new LKHttpMapperException(
                        errorJson(String.format("Not found: %s", target), "AAA001"),
                        HttpServletResponse.SC_NOT_FOUND
                    );
            }
        } catch (IOException e) {
            throw new LKHttpMapperException(
                errorJson("Unexpected server error", "AAA000"),
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e
            );
        }
    }

    static String errorJson(final String error, final String code) {
        final ObjectNode obj = JettyUtils.MAPPER.createObjectNode();
        obj.put("error", error);
        obj.put("code", code);
        return JettyUtils.toJsonString(obj);
    }

    private void hello(final HttpServletRequest req, final PrintWriter writer) {
        final ObjectNode obj = JettyUtils.MAPPER.createObjectNode();
        obj.put("message", "Hello World!");
        writer.println(JettyUtils.toJsonString(obj));
    }

    private void world(final HttpServletRequest req, final PrintWriter writer) {
        final ObjectNode obj = JettyUtils.MAPPER.createObjectNode();
        obj.put("message", "Hello World! 😀");
        writer.println(JettyUtils.toJsonString(obj));
    }

    private void echo(final HttpServletRequest req, final PrintWriter writer) {
        final ArrayNode arr = JettyUtils.MAPPER.createArrayNode();
        for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            arr.add(toKeyValueObject(e.getKey(), e.getValue()));
        }

        final ObjectNode obj = JettyUtils.MAPPER.createObjectNode();
        obj.replace("parameters", arr);
        writer.println(JettyUtils.toJsonString(obj));
    }

    private static ObjectNode toKeyValueObject(final String key, final String[] values) {
        final ObjectNode obj = JettyUtils.MAPPER.createObjectNode();
        obj.put("name", key);

        if (values.length == 0) {
            obj.put("value", "");
        } else if (values.length == 1) {
            obj.put("value", values[0]);
        } else {
            final ArrayNode arr = JettyUtils.MAPPER.createArrayNode();
            for (String value : values) {
                arr.add(value);
            }
            obj.replace("value", arr);
        }

        return obj;
    }
}

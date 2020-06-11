package org.lasantha.jetty;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

public final class JettyUtils {

    public static final ObjectMapper MAPPER = createObjectMapper();

    private JettyUtils() {}

    private static ObjectMapper createObjectMapper() {
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(format));
        javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(format));

        final ObjectMapper om =
            new ObjectMapper()
                .registerModule(new GuavaModule())
                .registerModule(new Jdk8Module())
                .registerModule(javaTimeModule);

        // Make sure time is written as a formatted string
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return om;
    }

    /**
     * Serializes the given object into a JSON blob.
     *
     * @param object Object to be serialized.
     * @return JSON representation of the object.
     */
    public static String toJsonString(final Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Deserializes the given object into a JSON blob.
     *
     * @param json  JSON representation of the object.
     * @param clazz Type of the object to be created.
     * @param <T>   Deserialized object.
     * @return The deserialized object.
     */
    public static <T> T fromJsonString(final String json, final Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * "/foo/?dfsfad" -> "foo"
     */
    public static String extractSubTarget(final String subTargetToBe) {
        int delimit = -1;
        for (int i = 0; i < subTargetToBe.length(); i++) {
            final char c = subTargetToBe.charAt(i);
            if (c == '/' || c == '?' || c == '#') {
                delimit = i;
                break;
            }
        }

        if (delimit >= 0) {
            return subTargetToBe.substring(0, delimit);
        }
        return subTargetToBe;
    }

    public static String shortenString(final String target, final int length) {
        if (target == null) {
            return null;
        }
        return target.length() > length ? (target.substring(0, length) + "...") : target;
    }

    public static String toLog(final long elapsed,
                               final String httpMethod,
                               final String target,
                               final int responseStatus) {
        final ObjectNode obj = JettyUtils.MAPPER.createObjectNode();
        obj.put("elapsed", elapsed);
        obj.put("method", httpMethod);
        obj.put("target", JettyUtils.shortenString(target, 97));
        obj.put("status", responseStatus);
        return JettyUtils.toJsonString(obj);
    }
}

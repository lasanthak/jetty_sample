package org.lasantha.jetty;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.MoreObjects;

public class LKHttpMapperException extends RuntimeException {

    private final int statusCode;

    public LKHttpMapperException(final String message) {
        super(message);
        statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    public LKHttpMapperException(final String message, final int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public LKHttpMapperException(final String message, final Throwable cause) {
        super(message, cause);
        statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    public LKHttpMapperException(final String message, final int statusCode, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("message", super.getMessage())
                          .add("statusCode", statusCode)
                          .toString();
    }
}

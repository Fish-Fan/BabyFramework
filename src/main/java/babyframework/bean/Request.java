package babyframework.bean;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

public class Request {
    /**
     * 请求的路径
     */
    private String RequestPath;
    /**
     * 请求的方法
     */
    private String RequestMethod;

    public Request(String requestMethod,String requestPath) {
        this.RequestMethod = requestMethod;
        this.RequestPath = requestPath;
    }

    public String getRequestPath() {
        return RequestPath;
    }

    public String getRequestMethod() {
        return RequestMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        return new EqualsBuilder()
                .append(RequestPath, request.RequestPath)
                .append(RequestMethod, request.RequestMethod)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(RequestPath)
                .append(RequestMethod)
                .toHashCode();
    }
}

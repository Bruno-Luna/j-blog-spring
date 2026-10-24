package br.com.blog.config.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class SecureRequestLoggingFilter extends OncePerRequestFilter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(SecureRequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        try {
            MDC.put("httpMethod", request.getMethod());
            MDC.put("uri", request.getRequestURI());
            MDC.put("user", resolveUsername());

            LOGGER.debug(buildBeforeMessage(request));

            filterChain.doFilter(wrappedRequest, response);

            String route = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            MDC.put("uri", route != null ? route : request.getRequestURI());
            MDC.put("status", String.valueOf(response.getStatus()));
            MDC.put("responseTime", String.valueOf(System.currentTimeMillis() - startTime));
            MDC.put("user", resolveUsername());

            String payload = extractPayload(wrappedRequest);
            if (!payload.isBlank()) {
                LOGGER.debug(Markers.appendRaw("payload", sanitize(payload)), buildAfterMessage(wrappedRequest));
            } else {
                LOGGER.debug(buildAfterMessage(wrappedRequest));
            }
        } finally {
            MDC.remove("httpMethod");
            MDC.remove("uri");
            MDC.remove("status");
            MDC.remove("responseTime");
            MDC.remove("user");
        }
    }

    private String buildBeforeMessage(HttpServletRequest request) {
        StringBuilder message = new StringBuilder("Before request [")
                .append(request.getMethod())
                .append(' ')
                .append(request.getRequestURI());

        if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
            message.append('?').append(request.getQueryString());
        }

        message.append(", client=").append(request.getRemoteAddr());

        String username = resolveUsername();
        if (!"anonymous".equals(username)) {
            message.append(", user=").append(username);
        }

        return message.append(']').toString();
    }

    private String buildAfterMessage(ContentCachingRequestWrapper request) {
        StringBuilder message = new StringBuilder("HTTP Request: ")
                .append(request.getMethod())
                .append(' ')
                .append(request.getRequestURI());

        if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
            message.append('?').append(request.getQueryString());
        }

        message.append(", client=").append(request.getRemoteAddr());

        String username = resolveUsername();
        if (!"anonymous".equals(username)) {
            message.append(", user=").append(username);
        }


        return message.toString();
    }

    private String extractPayload(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }

        Charset charset = Charset.forName(request.getCharacterEncoding());

        return new String(content, charset);
    }

    private String resolveUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        String username = authentication.getName();
        return username != null && !username.isBlank() && !"anonymousUser".equals(username)
                ? username
                : "anonymous";
    }

    private String sanitize(String message) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(message);
            maskPasswords(jsonNode);
            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (IOException ignored) {
            return message.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
                    .replaceAll("\\s+", " ")
                    .trim();
        }
    }

    private void maskPasswords(JsonNode jsonNode) {
        if (jsonNode instanceof ObjectNode objectNode) {
            objectNode.fieldNames().forEachRemaining(fieldName -> {
                JsonNode childNode = objectNode.get(fieldName);
                if ("password".equalsIgnoreCase(fieldName) && childNode != null && !childNode.isNull()) {
                    objectNode.put(fieldName, "***");
                } else {
                    maskPasswords(childNode);
                }
            });
        } else if (jsonNode instanceof ArrayNode arrayNode) {
            arrayNode.forEach(this::maskPasswords);
        }
    }
}

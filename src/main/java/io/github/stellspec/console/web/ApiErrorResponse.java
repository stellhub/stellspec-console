package io.github.stellspec.console.web;

import java.time.Instant;
import java.util.Map;

/** API 错误响应。 */
public record ApiErrorResponse(Instant timestamp, int status, String error, String message, Map<String, Object> details) {}

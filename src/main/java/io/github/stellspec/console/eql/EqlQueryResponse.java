package io.github.stellspec.console.eql;

import java.util.List;
import java.util.Map;

/** 前端 EQL 查询响应。 */
public record EqlQueryResponse(
        String id,
        boolean partial,
        boolean running,
        boolean timedOut,
        long took,
        Total total,
        List<Event> events,
        List<Sequence> sequences,
        Map<String, Object> request) {

    public record Total(long value, String relation) {}

    public record Event(String index, String id, boolean missing, Map<String, Object> source, Map<String, Object> fields) {}

    public record Sequence(List<Event> events, List<Object> joinKeys) {}
}

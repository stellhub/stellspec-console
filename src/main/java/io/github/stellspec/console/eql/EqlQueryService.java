package io.github.stellspec.console.eql;

import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.eql.EqlHits;
import co.elastic.clients.elasticsearch.eql.EqlSearchRequest;
import co.elastic.clients.elasticsearch.eql.EqlSearchResponse;
import co.elastic.clients.elasticsearch.eql.HitsEvent;
import co.elastic.clients.elasticsearch.eql.HitsSequence;
import co.elastic.clients.json.JsonData;
import io.github.stellflux.elaticsearch.StellfluxElaticsearchClient;
import io.github.stellspec.console.config.StellspecConsoleEqlProperties;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** EQL 查询服务。 */
@Service
@RequiredArgsConstructor
public class EqlQueryService {

    private final StellfluxElaticsearchClient elaticsearchClient;

    private final StellspecConsoleEqlProperties properties;

    /**
     * 执行 EQL 查询。
     *
     * @param request 前端查询请求
     * @return 查询响应
     * @throws IOException Elaticsearch 查询异常
     */
    public EqlQueryResponse query(EqlQueryRequest request) throws IOException {
        List<String> indices = effectiveIndex(request.index());
        int size = effectiveSize(request.size());
        int fetchSize = request.fetchSize() == null ? properties.getDefaultFetchSize() : request.fetchSize();
        String timestampField = effectiveText(request.timestampField(), properties.getTimestampField());
        String eventCategoryField = effectiveText(request.eventCategoryField(), properties.getEventCategoryField());

        EqlSearchRequest eqlRequest = EqlSearchRequest.of(
                builder ->
                        builder.index(indices)
                                .query(request.query())
                                .size(size)
                                .fetchSize(fetchSize)
                                .timestampField(timestampField)
                                .eventCategoryField(eventCategoryField)
                                .caseSensitive(request.caseSensitive())
                                .allowPartialSearchResults(properties.isAllowPartialSearchResults())
                                .allowPartialSequenceResults(properties.isAllowPartialSequenceResults()));

        EqlSearchResponse<Object> response = elaticsearchClient.eqlSearch(eqlRequest, Object.class);
        return toResponse(response, indices, request.query(), size, fetchSize, timestampField, eventCategoryField);
    }

    /**
     * 获取 EQL 查询配置状态。
     *
     * @return 配置状态
     */
    public Map<String, Object> status() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("module", "stellspec-console");
        status.put("defaultIndex", properties.getDefaultIndex());
        status.put("timestampField", properties.getTimestampField());
        status.put("eventCategoryField", properties.getEventCategoryField());
        status.put("defaultSize", properties.getDefaultSize());
        status.put("maxSize", properties.getMaxSize());
        status.put("defaultFetchSize", properties.getDefaultFetchSize());
        status.put("message", "StellSpec Console EQL query API is initialized");
        return status;
    }

    private EqlQueryResponse toResponse(
            EqlSearchResponse<Object> response,
            List<String> indices,
            String query,
            int size,
            int fetchSize,
            String timestampField,
            String eventCategoryField) {
        EqlHits<Object> hits = response.hits();
        List<EqlQueryResponse.Event> events =
                hits.events().stream().map(this::toEvent).toList();
        List<EqlQueryResponse.Sequence> sequences =
                hits.sequences().stream().map(this::toSequence).toList();
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("index", indices);
        request.put("query", query);
        request.put("size", size);
        request.put("fetchSize", fetchSize);
        request.put("timestampField", timestampField);
        request.put("eventCategoryField", eventCategoryField);
        return new EqlQueryResponse(
                response.id(),
                Boolean.TRUE.equals(response.isPartial()),
                Boolean.TRUE.equals(response.isRunning()),
                Boolean.TRUE.equals(response.timedOut()),
                response.took() == null ? 0L : response.took(),
                toTotal(hits.total()),
                events,
                sequences,
                request);
    }

    private EqlQueryResponse.Event toEvent(HitsEvent<Object> event) {
        return new EqlQueryResponse.Event(
                event.index(),
                event.id(),
                Boolean.TRUE.equals(event.missing()),
                toSourceMap(event.source()),
                toFieldMap(event.fields()));
    }

    private EqlQueryResponse.Sequence toSequence(HitsSequence<Object> sequence) {
        List<EqlQueryResponse.Event> events =
                sequence.events().stream().map(this::toEvent).toList();
        List<Object> joinKeys = sequence.joinKeys().stream().map(this::toJsonValue).toList();
        return new EqlQueryResponse.Sequence(events, joinKeys);
    }

    private EqlQueryResponse.Total toTotal(TotalHits total) {
        if (total == null) {
            return new EqlQueryResponse.Total(0L, "eq");
        }
        return new EqlQueryResponse.Total(total.value(), total.relation().jsonValue());
    }

    private Map<String, Object> toFieldMap(Map<String, List<JsonData>> fields) {
        Map<String, Object> result = new LinkedHashMap<>();
        fields.forEach((key, values) -> result.put(key, values.stream().map(this::toJsonValue).toList()));
        return result;
    }

    private Object toJsonValue(JsonData value) {
        return value == null ? null : value.to(Object.class);
    }

    private Map<String, Object> toSourceMap(Object source) {
        if (!(source instanceof Map<?, ?> sourceMap)) {
            return Map.of();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        sourceMap.forEach((key, value) -> result.put(String.valueOf(key), value));
        return result;
    }

    private List<String> effectiveIndex(List<String> index) {
        if (index == null || index.isEmpty()) {
            return properties.getDefaultIndex();
        }
        List<String> normalized = index.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
        return normalized.isEmpty() ? properties.getDefaultIndex() : normalized;
    }

    private int effectiveSize(Integer size) {
        int requestedSize = size == null ? properties.getDefaultSize() : size;
        return Math.min(requestedSize, properties.getMaxSize());
    }

    private String effectiveText(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}

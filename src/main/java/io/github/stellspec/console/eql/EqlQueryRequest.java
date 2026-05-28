package io.github.stellspec.console.eql;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** 前端 EQL 查询请求。 */
public record EqlQueryRequest(
        @NotBlank(message = "query must not be blank") String query,
        List<String> index,
        @Min(1) @Max(5000) Integer size,
        @Min(1) @Max(5000) Integer fetchSize,
        Boolean caseSensitive,
        String timestampField,
        String eventCategoryField) {}

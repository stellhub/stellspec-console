package io.github.stellspec.console.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** StellSpec Console EQL 查询配置。 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "stellspec.console.eql")
public class StellspecConsoleEqlProperties {

    /** 默认查询的 Elaticsearch 索引或 data stream pattern。 */
    private List<String> defaultIndex = List.of("logs-*-*");

    /** EQL 事件时间字段。 */
    private String timestampField = "@timestamp";

    /** EQL 事件分类字段。 */
    private String eventCategoryField = "event.category";

    /** 默认返回条数。 */
    @Min(1)
    @Max(1000)
    private int defaultSize = 100;

    /** 单次请求允许的最大返回条数。 */
    @Min(1)
    @Max(5000)
    private int maxSize = 500;

    /** 默认 fetch size。 */
    @Min(1)
    @Max(5000)
    private int defaultFetchSize = 100;

    /** 是否允许部分 search 结果。 */
    private boolean allowPartialSearchResults = true;

    /** 是否允许部分 sequence 结果。 */
    private boolean allowPartialSequenceResults = true;
}

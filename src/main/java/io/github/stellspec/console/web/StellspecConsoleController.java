package io.github.stellspec.console.web;

import io.github.stellspec.console.eql.EqlQueryRequest;
import io.github.stellspec.console.eql.EqlQueryResponse;
import io.github.stellspec.console.eql.EqlQueryService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** StellSpec Console HTTP 查询接口。 */
@RestController
@RequestMapping("/api/stellspec/console")
@RequiredArgsConstructor
public class StellspecConsoleController {

    private final EqlQueryService eqlQueryService;

    /**
     * 获取控制面查询服务状态。
     *
     * @return 服务状态
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return eqlQueryService.status();
    }

    /**
     * 执行前端传入的 EQL 查询。
     *
     * @param request 查询请求
     * @return 查询结果
     * @throws IOException Elaticsearch 查询异常
     */
    @PostMapping(
            value = "/eql/query",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public EqlQueryResponse query(@Valid @RequestBody EqlQueryRequest request) throws IOException {
        return eqlQueryService.query(request);
    }
}

package io.github.stellspec.console;

import io.github.stellspec.console.config.StellspecConsoleEqlProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** StellSpec Console 启动入口。 */
@SpringBootApplication
@EnableConfigurationProperties(StellspecConsoleEqlProperties.class)
public class StellspecConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(StellspecConsoleApplication.class, args);
    }
}

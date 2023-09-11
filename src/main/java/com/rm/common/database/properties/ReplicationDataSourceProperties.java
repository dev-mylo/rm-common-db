package com.rm.common.database.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.replication")
public class ReplicationDataSourceProperties {
    private String username;
    private String password;
    private String driverClassName;
    private Write write;
    private List<Read> reads;
    private String configLocationResource;
    private String mapperLocationResource;
    private int connectionTimeout;
    private int idleTimeout;
    private int maxLifetime;
    @Getter
    @Setter
    public static class Write {
        private String name;
        private String url;
        private int poolSize;
        private boolean readOnly;
    }
    @Getter
    @Setter
    public static class Read {
        private String name;
        private String url;
        private int poolSize;
        private boolean readOnly;
    }
}
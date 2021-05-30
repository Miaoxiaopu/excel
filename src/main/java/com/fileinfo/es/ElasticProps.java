package com.fileinfo.es;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "es")
public class ElasticProps {
    private String host;
    private int port;
    private String scheme;
    private String idxName;
}

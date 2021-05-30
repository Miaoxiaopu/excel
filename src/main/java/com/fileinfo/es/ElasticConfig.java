package com.fileinfo.es;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {

    @Autowired
    private ElasticProps elasticProps;

    @Autowired
    private RestClientBuilder restClientBuilder;

    @Bean
    public RestClientBuilder restClientBuilder() {
        return RestClient.builder(makeHttpHost());
    }

    @Bean
    public RestClient elasticsearchRestClient(){
        return RestClient.builder(new HttpHost(elasticProps.getHost(), elasticProps.getPort(), elasticProps.getScheme())).build();
    }

    private HttpHost makeHttpHost() {
        return new HttpHost(elasticProps.getHost(), elasticProps.getPort(), elasticProps.getScheme());
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return new RestHighLevelClient(restClientBuilder);
    }

}

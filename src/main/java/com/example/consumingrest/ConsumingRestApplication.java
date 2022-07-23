package com.example.consumingrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.client.UnknownContentTypeException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.http.MediaType;
import java.nio.charset.Charset;
import java.util.Arrays;

@SpringBootApplication
public class ConsumingRestApplication {

    private static final Logger log = LoggerFactory.getLogger(ConsumingRestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ConsumingRestApplication.class, args).close();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {
            ConsumingRestApplication.zip(restTemplate);
        };
    }
    
    private static void zip(RestTemplate restTemplate) {
        try {
            
            final String url = "http://zipcloud.ibsnet.co.jp/api/search?zipcode={zipcode}";
            Map<String, String> param = new HashMap<String, String>();
            param.put("zipcode","2500011");
            
            // zipcodeは「Content-Type: text/plain;charset=utf-8」を返してくるが、デフォルトのままでは処理できない。
            final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setSupportedMediaTypes(Arrays.asList(new MediaType(MediaType.TEXT_PLAIN, Charset.forName("utf-8"))));
            restTemplate.setMessageConverters(Arrays.asList(converter));

            ZipResult results = restTemplate.getForObject(url, ZipResult.class, param);
            log.info(results.toString());
        } catch (UnknownContentTypeException e) {
            log.error(e.toString());
            log.error(e.getTargetType().toString());
            throw e;
        }
        
    }
    
}
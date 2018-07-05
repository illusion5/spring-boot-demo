package top.yangyl.learn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportXmlConfig {

    @Bean
    public ImportXmlPostProcessor importXmlPostProcessor(){
        return new ImportXmlPostProcessor();
    }
}

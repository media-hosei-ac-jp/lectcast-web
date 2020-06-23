package jp.ac.hosei.media.lectcast.web.config;

import jp.ac.hosei.media.thymeleaf.dialect.HoseiDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LectcastConfig {

  @Bean
  public HoseiDialect hoseiDialect() {
    return new HoseiDialect();
  }

}

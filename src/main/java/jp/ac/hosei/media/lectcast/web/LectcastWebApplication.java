package jp.ac.hosei.media.lectcast.web;

import java.util.Collections;
import java.util.Map;
import javax.servlet.SessionTrackingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
@RestController
public class LectcastWebApplication {

  private final RequestMappingHandlerMapping handlerMapping;

  public LectcastWebApplication(RequestMappingHandlerMapping handlerMapping) {
    // refs. https://qiita.com/otsu-Miya/items/eac37b2720b67a58690b
    this.handlerMapping = handlerMapping;
    System.out.println("======= url mapping");
    final Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = this.handlerMapping
        .getHandlerMethods();
    for (Map.Entry<RequestMappingInfo, HandlerMethod> mapItem : handlerMethodMap.entrySet()) {
      final RequestMappingInfo key = mapItem.getKey();
      final HandlerMethod value = mapItem.getValue();
      System.out.println("======= " + key.getPatternsCondition() + " :: " + value);
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(LectcastWebApplication.class, args);
  }

  @Bean
  public ServletContextInitializer servletContextInitializer(
      @Value("${secure.cookie}") boolean secure) {

    return servletContext -> {
      servletContext.getSessionCookieConfig().setHttpOnly(true);
      servletContext.getSessionCookieConfig().setSecure(secure);
      servletContext.getSessionCookieConfig().setMaxAge(60);
      servletContext.setSessionTrackingModes(
          Collections.singleton(SessionTrackingMode.COOKIE)
      );
    };
  }

  @RequestMapping("/")
  String index() {
    return "It works!";
  }

}

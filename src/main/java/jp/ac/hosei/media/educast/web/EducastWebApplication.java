package jp.ac.hosei.media.educast.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@SpringBootApplication
@RestController
public class EducastWebApplication {

    private final RequestMappingHandlerMapping handlerMapping;

    public EducastWebApplication(RequestMappingHandlerMapping handlerMapping) {
        // refs. https://qiita.com/otsu-Miya/items/eac37b2720b67a58690b
        this.handlerMapping = handlerMapping;
        System.out.println("======= url mapping");
        final Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = this.handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> mapItem : handlerMethodMap.entrySet()) {
            final RequestMappingInfo key = mapItem.getKey();
            final HandlerMethod value = mapItem.getValue();
            System.out.println("======= " + key.getPatternsCondition() + " :: " + value);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(EducastWebApplication.class, args);
    }

    @RequestMapping("/")
    String index() {
        return "It works!";
    }

}

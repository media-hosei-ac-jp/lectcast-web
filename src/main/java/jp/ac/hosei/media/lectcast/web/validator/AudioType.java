package jp.ac.hosei.media.lectcast.web.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AudioTypeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AudioType {
    String message() default "{AudioType.message}";
    Class <?>[] groups() default{};
    Class<? extends Payload>[] payload() default{};
}

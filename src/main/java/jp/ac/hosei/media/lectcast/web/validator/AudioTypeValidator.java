package jp.ac.hosei.media.lectcast.web.validator;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;



public class AudioTypeValidator implements ConstraintValidator<AudioType, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value.isEmpty()) {
            return true;
        }
        final String contentName= value.getOriginalFilename().toLowerCase();
        final String[] supportType = {"mp3","m4a","wma"};

        if(Arrays.asList(supportType).contains(contentName.substring( contentName.length() -3 ))){

            return true;
        }

        return false;

    }
}

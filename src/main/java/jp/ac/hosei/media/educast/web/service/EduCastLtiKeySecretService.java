package jp.ac.hosei.media.educast.web.service;

import org.springframework.stereotype.Service;

@Service
public class EduCastLtiKeySecretService implements org.imsglobal.aspect.LtiKeySecretService {

    @Override
    public String getSecretForKey(final String key) {
        // FIXME: this key-value pair is temporary
        if (key.equals("key")) {
            return "secret";
        }
        return null;
    }

}

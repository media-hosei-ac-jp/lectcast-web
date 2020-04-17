package jp.ac.hosei.media.educast.web.service;

import jp.ac.hosei.media.educast.web.data.LtiKey;
import jp.ac.hosei.media.educast.web.repository.LtiKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EduCastLtiKeySecretService implements org.imsglobal.aspect.LtiKeySecretService {

    @Autowired
    private LtiKeyRepository ltiKeyRepository;

    @Override
    public String getSecretForKey(final String key) {
        final LtiKey ltiKey = ltiKeyRepository.findByKey(key);
        if (null != ltiKey) {
            return ltiKey.getSecret();
        }
        return null;
    }

}

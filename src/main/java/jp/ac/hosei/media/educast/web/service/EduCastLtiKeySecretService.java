package jp.ac.hosei.media.educast.web.service;

import jp.ac.hosei.media.educast.web.data.LtiKeypair;
import jp.ac.hosei.media.educast.web.repository.LtiKeypairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EduCastLtiKeySecretService implements org.imsglobal.aspect.LtiKeySecretService {

    @Autowired
    private LtiKeypairRepository ltiKeypairRepository;

    @Override
    public String getSecretForKey(final String ltiKey) {
        final LtiKeypair ltiKeypair = ltiKeypairRepository.findByLtiKey(ltiKey);
        if (null != ltiKeypair) {
            return ltiKeypair.getLtiSecret();
        }
        return null;
    }

}

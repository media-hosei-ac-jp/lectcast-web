package jp.ac.hosei.media.lectcast.web.service;

import jp.ac.hosei.media.lectcast.web.data.LtiKeypair;
import jp.ac.hosei.media.lectcast.web.repository.LtiKeypairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectcastLtiKeySecretService implements org.imsglobal.aspect.LtiKeySecretService {

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

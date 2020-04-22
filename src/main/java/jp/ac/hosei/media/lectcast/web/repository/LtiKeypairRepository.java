package jp.ac.hosei.media.lectcast.web.repository;

import jp.ac.hosei.media.lectcast.web.data.LtiKeypair;
import org.springframework.data.repository.CrudRepository;

public interface LtiKeypairRepository extends CrudRepository<LtiKeypair, Integer> {

    LtiKeypair findByLtiKey(String ltiKey);

}

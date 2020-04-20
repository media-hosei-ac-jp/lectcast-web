package jp.ac.hosei.media.educast.web.repository;

import jp.ac.hosei.media.educast.web.data.LtiKeypair;
import org.springframework.data.repository.CrudRepository;

public interface LtiKeypairRepository extends CrudRepository<LtiKeypair, Integer> {

    LtiKeypair findByLtiKey(String ltiKey);

}

package jp.ac.hosei.media.educast.web.repository;

import jp.ac.hosei.media.educast.web.data.LtiKey;
import org.springframework.data.repository.CrudRepository;

public interface LtiKeyRepository extends CrudRepository<LtiKey, Integer> {

    LtiKey findByKey(String key);

}

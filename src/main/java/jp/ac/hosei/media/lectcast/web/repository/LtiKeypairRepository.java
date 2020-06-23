package jp.ac.hosei.media.lectcast.web.repository;

import jp.ac.hosei.media.lectcast.web.data.LtiKeypair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LtiKeypairRepository extends JpaRepository<LtiKeypair, Integer> {

  LtiKeypair findByLtiKey(String ltiKey);

}

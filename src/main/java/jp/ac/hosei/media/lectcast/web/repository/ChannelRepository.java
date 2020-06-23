package jp.ac.hosei.media.lectcast.web.repository;

import jp.ac.hosei.media.lectcast.web.data.Channel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Integer> {

  Channel findById(String id);

  Channel findByLtiContextId(String ltiContextId);

  Channel findByLtiContextIdAndLtiResourceLinkId(String ltiContextId, String ltiResourceLinkId);

}

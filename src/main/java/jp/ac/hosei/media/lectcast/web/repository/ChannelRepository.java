package jp.ac.hosei.media.lectcast.web.repository;

import jp.ac.hosei.media.lectcast.web.data.Channel;
import org.springframework.data.repository.CrudRepository;

public interface ChannelRepository extends CrudRepository<Channel, Integer> {

    Channel findByLtiContextId(String ltiContextId);

    Channel findByLtiContextIdAndLtiResourceLinkId(String ltiContextId, String ltiResourceLinkId);

}

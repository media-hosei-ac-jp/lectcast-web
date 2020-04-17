package jp.ac.hosei.media.educast.web.repository;

import jp.ac.hosei.media.educast.web.data.Channel;
import org.springframework.data.repository.CrudRepository;

public interface ChannelRepository extends CrudRepository<Channel, Integer> {

    Channel findByContextIdAndResourceLinkId(String contextId, String resourceLinkId);

}

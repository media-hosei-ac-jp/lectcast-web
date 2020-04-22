package jp.ac.hosei.media.lectcast.web.repository;

import jp.ac.hosei.media.lectcast.web.data.Channel;
import jp.ac.hosei.media.lectcast.web.data.Feed;
import org.springframework.data.repository.CrudRepository;

public interface FeedRepository extends CrudRepository<Feed, Integer> {

    Feed findById(String id);

    Feed findByChannelAndLtiUserId(Channel channel, String ltiUserId);

}

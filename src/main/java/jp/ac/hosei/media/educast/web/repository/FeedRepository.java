package jp.ac.hosei.media.educast.web.repository;

import jp.ac.hosei.media.educast.web.data.Channel;
import jp.ac.hosei.media.educast.web.data.Feed;
import org.springframework.data.repository.CrudRepository;

public interface FeedRepository extends CrudRepository<Feed, Integer> {

    Feed findById(String id);

    Feed findByChannelAndLtiUserId(Channel channel, String ltiUserId);

}

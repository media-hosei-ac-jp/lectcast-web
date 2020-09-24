package jp.ac.hosei.media.lectcast.web.repository;

import jp.ac.hosei.media.lectcast.web.data.Channel;
import jp.ac.hosei.media.lectcast.web.data.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {

  Iterable<Item> findAllByChannelOrderByCreatedAtDesc(Channel channel);

  @Query(value = "select * from item where is_deleted = 0 and channel_id = ?1 and (is_infinity = 1 or now() between date_from and date_to) order by created_at desc", nativeQuery = true)
  Iterable<Item> findActiveByChannelOrderByCreatedAtDesc(Channel channel);

  Item findByIdAndChannel(String id, Channel channel);

}

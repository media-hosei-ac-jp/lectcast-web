package jp.ac.hosei.media.lectcast.web.repository;

import jp.ac.hosei.media.lectcast.web.data.Channel;
import jp.ac.hosei.media.lectcast.web.data.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {

  Iterable<Item> findAllByChannel(Channel channel);

  Item findByIdAndChannel(String id, Channel channel);

}

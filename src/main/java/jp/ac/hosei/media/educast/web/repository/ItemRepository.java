package jp.ac.hosei.media.educast.web.repository;

import jp.ac.hosei.media.educast.web.data.Channel;
import jp.ac.hosei.media.educast.web.data.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer> {

    Iterable<Item> findAllByChannel(Channel channel);
    
}

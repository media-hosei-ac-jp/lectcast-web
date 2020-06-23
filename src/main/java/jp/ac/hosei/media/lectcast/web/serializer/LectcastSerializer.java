package jp.ac.hosei.media.lectcast.web.serializer;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

public class LectcastSerializer extends JdkSerializationRedisSerializer {

  @Override
  public Object deserialize(@Nullable byte[] bytes) {
    try {
      return super.deserialize(bytes);
    } catch (SerializationException ex) {
      // Called when session value deserialize fails
      return null;
    }
  }

}


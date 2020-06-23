package jp.ac.hosei.media.lectcast.web.generator;

import java.io.Serializable;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

public class UuidGenerator extends IdentityGenerator {

  @Override
  public Serializable generate(SharedSessionContractImplementor s, Object obj) {
    return UUID.randomUUID().toString();
  }

}

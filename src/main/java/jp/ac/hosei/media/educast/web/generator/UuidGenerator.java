package jp.ac.hosei.media.educast.web.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;
import java.util.UUID;

public class UuidGenerator extends IdentityGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor s, Object obj) {
        return UUID.randomUUID().toString();
    }

}

package jp.ac.hosei.media.lectcast.web.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LtiKeypair implements Serializable {

    @Id
    private String ltiKey;

    private String ltiSecret;

    private String description;

}

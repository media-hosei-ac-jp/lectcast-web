package jp.ac.hosei.media.lectcast.web.data;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

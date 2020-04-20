package jp.ac.hosei.media.educast.web.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LtiKeypair {

    @Id
    private String ltiKey;

    private String ltiSecret;

    private String description;

    public String getLtiKey() {
        return ltiKey;
    }

    public void setLtiKey(final String key) {
        this.ltiKey = key;
    }

    public String getLtiSecret() {
        return ltiSecret;
    }

    public void setLtiSecret(final String ltiSecret) {
        this.ltiSecret = ltiSecret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

}

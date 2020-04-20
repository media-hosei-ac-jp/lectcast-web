package jp.ac.hosei.media.educast.web.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@EqualsAndHashCode
@ToString
@Table(name = "feed")
public class Feed {

    @Id
    @GenericGenerator(name = "UuidGenerator", strategy = "jp.ac.hosei.media.educast.web.generator.UuidGenerator")
    @GeneratedValue(generator = "UuidGenerator")
    private String id;

    @ManyToOne
    private Channel channel;

    private String ltiUserId;

    private int active;

    @NotNull
    private Date createdAt;

    @NotNull
    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getLtiUserId() {
        return ltiUserId;
    }

    public void setLtiUserId(String ltiUserId) {
        this.ltiUserId = ltiUserId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void onPrePersist() {
        setCreatedAt(new Date());
        setUpdatedAt(new Date());
    }

    @PreUpdate
    public void onPreUpdate() {
        setUpdatedAt(new Date());
    }

}

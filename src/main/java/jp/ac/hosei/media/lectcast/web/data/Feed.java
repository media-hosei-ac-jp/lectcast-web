package jp.ac.hosei.media.lectcast.web.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@EqualsAndHashCode
@ToString
@Table(name = "feed")
public class Feed implements Serializable {

  @Id
  @GenericGenerator(name = "UuidGenerator", strategy = "jp.ac.hosei.media.lectcast.web.generator.UuidGenerator")
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

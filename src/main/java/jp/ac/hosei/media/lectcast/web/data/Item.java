package jp.ac.hosei.media.lectcast.web.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
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
import org.hibernate.annotations.Where;

@Entity
@EqualsAndHashCode
@ToString
@Table(name = "item")
@Where(clause = "is_deleted = 0")
public class Item implements Serializable {

  @Id
  @GenericGenerator(name = "UuidGenerator", strategy = "jp.ac.hosei.media.lectcast.web.generator.UuidGenerator")
  @GeneratedValue(generator = "UuidGenerator")
  private String id;

  @ManyToOne
  private Channel channel;

  @Column(name = "s3_key")
  private String s3Key;

  private String title;

  private String description;

  private int duration;

  private int explicit;

  private int isDeleted;

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

  public String getS3Key() {
    return s3Key;
  }

  public void setS3Key(String s3Key) {
    this.s3Key = s3Key;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getExplicit() {
    return explicit;
  }

  public void setExplicit(int explicit) {
    this.explicit = explicit;
  }

  public int getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(int isDeleted) {
    this.isDeleted = isDeleted;
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

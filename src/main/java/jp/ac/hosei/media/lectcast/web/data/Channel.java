package jp.ac.hosei.media.lectcast.web.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@EqualsAndHashCode
@ToString
@Table(name = "channel")
public class Channel implements Serializable {

    @Id
    @GenericGenerator(name = "UuidGenerator", strategy = "jp.ac.hosei.media.lectcast.web.generator.UuidGenerator")
    @GeneratedValue(generator = "UuidGenerator")
    private String id;

    private String ltiContextId;

    private String ltiResourceLinkId;

    private String title;

    private String author;

    private String description;

    private int explicit;

    private String image;

    private String language;

    @NotNull
    private Date createdAt;

    @NotNull
    private Date updatedAt;

    @OneToMany(mappedBy = "channel")
    @OrderBy("createdAt DESC")
    private List<Item> itemList;

    @OneToMany(mappedBy = "channel")
    private List<Feed> feedList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLtiContextId() {
        return ltiContextId;
    }

    public void setLtiContextId(String contextId) {
        this.ltiContextId = contextId;
    }

    public String getLtiResourceLinkId() {
        return ltiResourceLinkId;
    }

    public void setLtiResourceLinkId(String resourceLinkId) {
        this.ltiResourceLinkId = resourceLinkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExplicit() {
        return explicit;
    }

    public void setExplicit(int explicit) {
        this.explicit = explicit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
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

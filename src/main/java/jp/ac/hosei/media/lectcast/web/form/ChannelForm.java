package jp.ac.hosei.media.lectcast.web.form;

import javax.validation.constraints.NotNull;

public class ChannelForm {

  @NotNull
  private String title;

  private String description;

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

}

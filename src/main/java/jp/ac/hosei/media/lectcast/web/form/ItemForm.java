package jp.ac.hosei.media.lectcast.web.form;

import java.util.Date;
import javax.validation.constraints.*;

import org.springframework.web.multipart.MultipartFile;

public class ItemForm {

  private MultipartFile audioFile;

  @Size(max=255)
  @NotEmpty
  private String title;

  @Size(max=4096)
  private String description;

  @NotNull
  private String isInfinity;

  @Future
  private Date dateFrom;

  private Date dateTo;

  public MultipartFile getAudioFile() {
    return audioFile;
  }

  public void setAudioFile(MultipartFile audioFile) {
    this.audioFile = audioFile;
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

  public String getIsInfinity() {
    return isInfinity;
  }

  public void setIsInfinity(String isInfinity) {
    this.isInfinity = isInfinity;
  }

  public Date getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(Date dateFrom) {
    this.dateFrom = dateFrom;
  }

  public Date getDateTo() {
    return dateTo;
  }

  public void setDateTo(Date dateTo) {
    this.dateTo = dateTo;
  }
}

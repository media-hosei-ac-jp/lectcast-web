package jp.ac.hosei.media.lectcast.web.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class ItemForm {

    @NotNull
    private MultipartFile audioFile;

    @NotNull
    private String title;

    private String description;

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

}

package dn.ute.shakya.models;

import java.io.Serializable;

public class Word implements Serializable {
    private long id;
    private long lessonId;
    private String content;

    public Word() {

    }

    public Word(long lessonId, String content) {
        this.lessonId = lessonId;
        this.content = content;
    }

    public Word(long id, long lessonId, String content) {
        this.id = id;
        this.lessonId = lessonId;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}

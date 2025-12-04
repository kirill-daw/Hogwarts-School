package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "avatars")
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;
    private long fileSize;
    private String mediaType;

    @Column(columnDefinition = "BYTEA")
    @JsonIgnore
    private byte[] data;

    @OneToOne
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;

    public Avatar() {
    }

    public Avatar(Long id, String filePath, long fileSize, String mediaType) {
        this.id = id;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
    }

    public Long getId() {
        return id;
    }
    public String getFilePath() {
        return filePath;
    }
    public long getFileSize() {
        return fileSize;
    }
    public String getMediaType() {
        return mediaType;
    }
    public byte[] getData() {
        return data;
    }
    public Student getStudent() {
        return student;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
}
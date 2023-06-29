package javharbek.starter.dto.files;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileDTO implements Serializable {
    String extension;
    String url;
    String id;
    String title;
}

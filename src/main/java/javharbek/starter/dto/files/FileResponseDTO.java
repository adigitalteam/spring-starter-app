package javharbek.starter.dto.files;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileResponseDTO {
    String id;
    String title;
    String description;
    Long size;
    String file;
    String extension;
    int status;
    Boolean isDeleted = false;
    String host;
    String absoluteUrl;
}

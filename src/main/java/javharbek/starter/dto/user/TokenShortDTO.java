package javharbek.starter.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenShortDTO {
    private Long id;

    private String token;

    private String data;

    private String data_type;

    private String type;

    private String status;

    private Integer expired_duration;

    private String identity_id;
}

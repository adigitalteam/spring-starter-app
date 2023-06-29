package javharbek.starter.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthTokenDTO {
    private Long id;
    private String token;
    private String data;
    private String data_type;
    private String type;
    private String status;
    private Integer expiredDuration;
    private String identityId;
}

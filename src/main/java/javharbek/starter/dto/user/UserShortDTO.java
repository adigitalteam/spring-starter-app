package javharbek.starter.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserShortDTO {
    private Long id;

    private String userName;

    private String email;
    private String title;

    private String phone;

    private String passport;

    private String personalIdentificationNumber;

    private Object fidoGspIdentity;

    private String avatar;

    private String inn;

}

package javharbek.starter.dto.user;

import lombok.Data;

@Data
public class LoginErrorResponseDTO {
    private String error;
    private String error_description;

}

package javharbek.starter.dto.user;

import lombok.Data;

@Data
public class TokenConfirmDTO {
    private String token;
    private String secret;
    private String data;
}

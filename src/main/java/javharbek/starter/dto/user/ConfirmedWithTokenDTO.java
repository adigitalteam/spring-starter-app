package javharbek.starter.dto.user;

import lombok.Data;

@Data
public class ConfirmedWithTokenDTO {
    TokenShortDTO token;
    UserShortDTO user;
    GetTokenDTO login;
}

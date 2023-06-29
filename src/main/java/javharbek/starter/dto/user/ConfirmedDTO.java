package javharbek.starter.dto.user;

import lombok.Data;

@Data
public class ConfirmedDTO {
    TokenShortDTO token;
    UserShortDTO user;
}

package javharbek.starter.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class CheckTokenResponseDTO {
    private String user_name;
    private List<String> scope;
    private boolean active;
    private int exp;
    private List<String> authorities;
    private String jti;
    private String client_id;
}

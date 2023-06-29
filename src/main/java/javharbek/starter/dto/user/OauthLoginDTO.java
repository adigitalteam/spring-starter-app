package javharbek.starter.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OauthLoginDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public LoginDTO toLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setLogin(this.username);
        dto.setPassword(this.password);
        return dto;
    }
}

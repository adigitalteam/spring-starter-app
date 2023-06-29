package javharbek.starter.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class MeDTO {
    String username;
    String title;
    String email;
    String phone;
    List<String> authorities;
    String status;
    String avatar;
    String personalIdentificationNumber;
    String passport;
    String inn;
    Long id;
}

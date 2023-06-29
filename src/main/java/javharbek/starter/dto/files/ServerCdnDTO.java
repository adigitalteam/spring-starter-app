package javharbek.starter.dto.files;

import lombok.Data;

@Data
public class ServerCdnDTO {
    String host;
    String username;
    String password;
    String uploadPath;
    String publicPath;
    String publicFilePath;
    String publicGlobalPath;
    String httpHost;
    String alias;
    int port = 22;
}

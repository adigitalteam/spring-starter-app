package javharbek.starter.config.jwt;

import com.google.gson.Gson;
import javharbek.starter.dto.ServerResponseDTO;
import javharbek.starter.dto.user.CheckTokenResponseDTO;
import javharbek.starter.dto.user.MeDTO;
import javharbek.starter.exceptions.AppException;
import lombok.Data;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;


@Component
@Data
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.auth-server-client-id}")
    private String client_id;
    @Value("${security.auth-server-client-secret}")
    private String client_secret;
    @Value("${security.auth-server-url}")
    private String auth_server;
    @Value("${security.auth-server-url-me}")
    private String auth_server_me;
    private CheckTokenResponseDTO checkTokenResponseDTO;
    private MeDTO meDTO;
    private String token;



    public String getUserNameFromJwtToken(String token) throws IOException {
        if (!token.equals(getToken())) {
            validateJwtToken(token);
        }
        return checkTokenResponseDTO.getUser_name();
    }

    public boolean validateJwtToken(String authToken) throws IOException {
        try {
            CheckTokenResponseDTO responseDTO = getCheckedToken(authToken);
            MeDTO me = getMe(authToken);
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    public CheckTokenResponseDTO getCheckedToken(String authToken) throws IOException {
        Gson gson = new Gson();
        String decodeBasic = (client_id + ":" + client_secret);
        String basic = Base64.getEncoder().encodeToString(decodeBasic.getBytes());
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(auth_server + "?token=" + authToken)
                .method("POST", body)
                .addHeader("Authorization", "Basic " + basic)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        CheckTokenResponseDTO checkTokenResponseDTO = gson.fromJson(responseBody.string(), CheckTokenResponseDTO.class);
        setCheckTokenResponseDTO(checkTokenResponseDTO);
        setToken(authToken);
        return checkTokenResponseDTO;
    }


    public MeDTO getMe(String authToken) throws IOException, AppException {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(auth_server_me)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String data = responseBody.string();

        if (response.code() != HttpStatus.SC_OK) {
            ServerResponseDTO serverResponseDTO = gson.fromJson(data, ServerResponseDTO.class);
            throw new AppException(serverResponseDTO.getMessage() + ":" + data);
        }

        MeDTO meDTO = gson.fromJson(data, MeDTO.class);
        setMeDTO(meDTO);
        return meDTO;
    }


}

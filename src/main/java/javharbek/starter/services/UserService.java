package javharbek.starter.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javharbek.starter.config.jwt.JwtUtils;
import javharbek.starter.dto.ServerResponseDTO;
import javharbek.starter.dto.user.*;
import javharbek.starter.entities.core.User;
import javharbek.starter.enums.SSOTypeEnum;
import javharbek.starter.exceptions.AppException;
import javharbek.starter.exceptions.LoginException;
import javharbek.starter.repositories.core.UserRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private JwtUtils jwtUtils;

    @Autowired
    public void setJwtUtils(@Lazy JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    public JwtUtils getJwtUtils(){
        return this.jwtUtils;
    }

    @Value("${security.auth-server}")
    private String auth_server;
    @Value("${security.auth-server-client-id}")
    private String client_id;
    @Value("${security.auth-server-client-secret}")
    private String client_secret;

    public MeDTO getMe(String tokenValue) throws AppException, IOException {
        MeDTO meDTO = jwtUtils.getMe(tokenValue);
        sync(meDTO);
        return meDTO;
    }

    public GetTokenDTO loginWithOutSync(LoginDTO loginDTO) throws IOException, AppException {
        Gson gson = new Gson();

        Map<Object, Object> of = Map.of(
                "login", loginDTO.getLogin(),
                "password", loginDTO.getPassword()
        );

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, objectMapper.writeValueAsString(of));
        Request request = new Request.Builder()
                .url(auth_server + "/oauth/users/login-hr")
                .method("POST", body)
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String data = responseBody.string();

        int code = response.code();
        Map<String, Object> map = gson.fromJson(data, Map.class);

        if (code == HttpStatus.SC_UNAUTHORIZED) {


            map.put("exist", false);

            throw new LoginException(map.get("message").toString(), map);
        }

        if (code == HttpStatus.SC_BAD_REQUEST) {

            map.put("exist", true);

            throw new LoginException(map.get("message").toString(), map);

        }


        if (code != HttpStatus.SC_OK) {
            ServerResponseDTO serverResponseDTO = gson.fromJson(data, ServerResponseDTO.class);
            throw new AppException(serverResponseDTO.getMessage());
        }


        return gson.fromJson(data, GetTokenDTO.class);
    }

    public GetTokenDTO login(LoginDTO loginDTO) throws IOException, AppException {
        GetTokenDTO getTokenDTO = loginWithOutSync(loginDTO);
        sync(jwtUtils.getMe(getTokenDTO.getAccess_token()));
        return getTokenDTO;
    }


    public GetTokenDTO logout(String token) throws IOException {

        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(auth_server + "/oauth/users/logout")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Response response = client.newCall(request).execute();

        ResponseBody responseBody = response.body();
        if (response.code() != HttpStatus.SC_OK) {
            LoginErrorResponseDTO loginErrorResponseDTO = gson.fromJson(responseBody.string(), LoginErrorResponseDTO.class);
            throw new RuntimeException(loginErrorResponseDTO.getError_description());
        }


        return gson.fromJson(responseBody.string(), GetTokenDTO.class);
    }

    public User getOrCreateFromMe(MeDTO meDTO) {
        String ssoId = String.valueOf(meDTO.getId());
        Optional<User> userOptional = userRepository.findBySsoIdAndSsoTypeAndIsDeletedFalse(ssoId, SSOTypeEnum.SSO_XALQ_BANK);
        User user = new User();
        if (userOptional.isPresent()) {
            user = userOptional.orElseThrow();
        }

        user.setUserName(meDTO.getUsername());
        user.setName(meDTO.getTitle());
        user.setEmail(meDTO.getEmail());
        user.setPhone(meDTO.getPhone());
        user.setAuthorities(meDTO.getAuthorities());
        user.setPassport(meDTO.getPassport());
        user.setPersonalIdentificationNumber(meDTO.getPersonalIdentificationNumber());
        user.setSsoId(ssoId);
        user.setSsoType(SSOTypeEnum.SSO_XALQ_BANK);

        return userRepository.save(user);


    }

    public User sync(MeDTO meDTO){
        return getOrCreateFromMe(meDTO);
    }

    public AuthTokenDTO register(RegisterDTO registerDTO) throws IOException, AppException {
        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, objectMapper.writeValueAsString(registerDTO));
        Request request = new Request.Builder()
                .url(auth_server + "/oauth/users/registerByPhone")
                .method("POST", body)
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String data = responseBody.string();

        if (!response.isSuccessful()) {
            throw new AppException("Ошибка при регестрации!");
        }

        return gson.fromJson(data, AuthTokenDTO.class);
    }
    public ConfirmedWithTokenDTO tokenConfirm(TokenConfirmDTO tokenConfirmDTO) throws IOException, AppException {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"token\": \"" + tokenConfirmDTO.getToken() + "\",\r\n    \"secret\": \"" + tokenConfirmDTO.getSecret() + "\",\r\n    \"data\": \"" + tokenConfirmDTO.getData() + "\"\r\n}");
        Request request = new Request.Builder()
                .url(auth_server + "/v1/auth-token/confirm?access")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        if (response.code() == HttpStatus.SC_BAD_REQUEST) {
            ServerResponseDTO serverResponseDTO = gson.fromJson(responseBody.string(), ServerResponseDTO.class);
            throw new RuntimeException(serverResponseDTO.getMessage());
        }
        ConfirmedDTO confirmedDTO = gson.fromJson(responseBody.string(), ConfirmedDTO.class);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLogin(confirmedDTO.getToken().getData());
        loginDTO.setPassword(tokenConfirmDTO.getSecret());
        GetTokenDTO getTokenDTO = login(loginDTO);

        ConfirmedWithTokenDTO confirmedWithTokenDTO = new ConfirmedWithTokenDTO();
        confirmedWithTokenDTO.setToken(confirmedDTO.getToken());
        confirmedWithTokenDTO.setUser(confirmedDTO.getUser());
        confirmedWithTokenDTO.setLogin(getTokenDTO);
        return confirmedWithTokenDTO;
    }


}

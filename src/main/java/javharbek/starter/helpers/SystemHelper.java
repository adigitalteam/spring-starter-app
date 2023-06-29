package javharbek.starter.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import javharbek.starter.dto.user.GetTokenDTO;
import javharbek.starter.dto.user.LoginDTO;
import javharbek.starter.exceptions.AppException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemHelper {
    public static UserDetails getCurrentUser() throws AppException {
        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            throw new AppException("Not Auth");
        }
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getCurrentUserName() throws AppException {
        return getCurrentUser().getUsername();
    }

    public static String getCurrentUserNameIgnore() {
        try {
            return getCurrentUserName();
        } catch (Exception exception) {
            return "";
        }
    }

    public static UserDetails getCurrentUserIgnore() {
        try {

            return getCurrentUser();
        } catch (Exception exception) {
            return null;
        }
    }


    @Value("${security.auth-server-login}")
    private String login;
    @Value("${security.auth-server-password}")
    private String password;
    @Value("${security.auth-server}")
    private String authEndpoint;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Gson gson;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public static String generateRandomString(int length) {
        if (length <= 0) length = 10;
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    public static String abbrevName(String name) {
        try {

            String[] names = name.split(" ");

            if (names.length == 3) {
                return LetterNameFilter(names) + "." + capitalize(names[0]);
            } else if (names.length == 4) {
                return LetterNameFilter(names) + "." + capitalize(names[0]);
            } else if (names.length == 2) {
                return LetterNameFilter(names) + "." + capitalize(names[0]);
            } else {
                return capitalize(name);
            }

        } catch (Exception exception) {
            return capitalize(name);
        }

    }

    public static String LetterNameFilter(String[] names) {
        if ((names[1].charAt(0) == 's' || names[1].charAt(0) == 'S') && (names[1].charAt(1) == 'h' || names[1].charAt(1) == 'H')) {
            return "Sh";
        }
        return String.valueOf(names[1].charAt(0));
    }


    public static String capitalize(String str) {

        if (str == null || str.length() == 0) return str;

        return str.substring(0, 1).toUpperCase() + str.substring(1);

    }

    public static long generateRandomNumberStatic(long max, long min) {
        return (long) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static String readFile(final String relFilePath) throws IOException {
        final URL url = Resources.getResource(relFilePath);
        return Resources.toString(url, StandardCharsets.UTF_8);
    }

    public String getToken(String headerAuth) {
        return headerAuth.replace("Bearer", "").trim();
    }

    public String getTokenFromRequest() {
        String authorization = getRequestAuthorizationHeader();
        return getToken(authorization);
    }

    public String getRequestAuthorizationHeader() {
        return getRequestHeader("Authorization");
    }

    public Boolean hasRequestAuthorizationHeader() {
        return hasRequestHeader("Authorization");
    }

    public String getRequestHeader(String name) {
        return httpServletRequest.getHeader(name);
    }

    public Boolean hasRequestHeader(String name) {
        return getRequestHeader(name) != null || getRequestHeader(name).length() == 0;
    }

    public Request.Builder addCurrentAuthBearerToken(Request.Builder builder) {
        if (!hasRequestAuthorizationHeader()) {
            return builder;
        }
        return builder.addHeader("Authorization", "Bearer " + getTokenFromRequest());
    }

    public Request.Builder addSystemAuthBearerToken(Request.Builder builder) throws AppException, IOException {
        GetTokenDTO getTokenDTO = getOauthTokenSystem();
        System.out.println("TOKEN AUTH: " + getTokenDTO.getAccess_token());
        return builder.addHeader("Authorization", "Bearer " + getTokenDTO.getAccess_token());
    }

    public GetTokenDTO getOauthTokenSystem() throws IOException, AppException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        LoginDTO loginDTO = new LoginDTO(login, password);

        RequestBody body = RequestBody.create(mediaType, objectMapper.writeValueAsString(loginDTO));
        Request request = new Request.Builder()
                .url(authEndpoint + "/oauth/users/login")
                .method("POST", body)
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new AppException("Auth Error HRMS");
        }
        ResponseBody responseBody = response.body();
        String responseData = responseBody.string();
        return gson.fromJson(responseData, GetTokenDTO.class);
    }

    public long generateRandomNumber(long max, long min) {
        return (long) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public long generateRandomNumberId() {
        long min = 100000000;
        long max = 999999999;
        return generateRandomNumber(max, min);
    }



    private static final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };
    private static final SSLContext trustAllSslContext;
    private static final SSLSocketFactory trustAllSslSocketFactory;

    static {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL");
            trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public static OkHttpClient getHttpClient() {
        return new OkHttpClient().newBuilder()
                .sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((e, v) -> true)
                .connectTimeout(120, TimeUnit.MINUTES)
                .connectTimeout(120, TimeUnit.MINUTES)
                .writeTimeout(120, TimeUnit.MINUTES)
                .readTimeout(120, TimeUnit.MINUTES)
                .build();
    }

    public static boolean isUseVar(String string,String varName) {
        if (string == null) {
            return false;
        }
        string = string.trim();
        if (string == null) {
            return false;
        }


        if(varName.startsWith("$")){
            varName = varName.replace("$","");
        }

        final String regex =  "\\$".concat(varName);
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }



    public static HashMap<String, String> hashMapObjectToHashMapString(HashMap<String, Object> map) {
        HashMap<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            if (entry.getKey().trim().length() <= 1) {
                continue;
            }


            try {

                newMap.put(entry.getKey(), (String) entry.getValue().toString());

            } catch (Exception exception) {

            }
        }
        return newMap;
    }

    public static HashMap<String, String> onlyOfficeWrapVarsCode(HashMap<String, String> map) {
        HashMap<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            if (entry.getKey().trim().length() <= 1) {
                continue;
            }


            newMap.put("$" + entry.getKey(), (String) entry.getValue().toString());
        }
        return newMap;
    }

    public static HashMap<String, String> onlyOfficeWrapVarsCodeString(HashMap<String, Object> map) {
        HashMap<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            if (entry.getKey().trim().length() <= 1) {
                continue;
            }


            if (entry.getValue() instanceof String) {
                newMap.put("$" + entry.getKey(), (String) entry.getValue().toString());
            }
        }
        return newMap;
    }

    public static HashMap<String, String> WrapVarsCodeString(HashMap<String, String> map) {
        HashMap<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            if (entry.getKey().trim().length() <= 1) {
                continue;
            }

            newMap.put("$" + entry.getKey(), entry.getValue());
        }
        return newMap;
    }


}

package javharbek.starter.helpers;

import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@NoArgsConstructor
public class FileHelper {
    public static void downloadFileWithAuth(URL url, String outputFileName, String authToken) throws IOException {
        OkHttpClient client = SystemHelper.getHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        FileOutputStream fos = new FileOutputStream(outputFileName);
        fos.write(response.body().bytes());
        fos.close();
    }

    public static void downloadFileWithoutAuth(URL url, String outputFileName) throws IOException {
        OkHttpClient client = SystemHelper.getHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }
        FileOutputStream fos = new FileOutputStream(outputFileName);
        fos.write(response.body().bytes());
        fos.close();
    }

    public static byte[] downloadFileWithoutAuth(String url) throws IOException {
        OkHttpClient client = SystemHelper.getHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to download file: " + response);
        }

        return response.body().bytes();
    }

    public static String downloadFileToTmp(URL url, String authToken) throws IOException {
        File tmpFile = File.createTempFile("onlyoffice-", "-file");
        String path = tmpFile.getPath();
        downloadFileWithAuth(url, path, authToken);
        tmpFile.deleteOnExit();
        return path;
    }


    public static String downloadFileToTmp(URL url) throws IOException {
        File tmpFile = File.createTempFile("onlyoffice-", "-file");
        String path = tmpFile.getPath();
        downloadFileWithoutAuth(url, path);
        tmpFile.deleteOnExit();
        return path;
    }

    public static String downloadFileToTmp(String url, String authToken) throws IOException {
        URL urlObject = new URL(url);
        return downloadFileToTmp(urlObject, authToken);
    }

    public static String downloadFileToTmp(String url) throws IOException {
        URL urlObject = new URL(url);
        return downloadFileToTmp(urlObject);
    }

    public static String saveStrToFile(String str, String outputFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(str.getBytes());
        fos.close();
        return outputFile;
    }

    public static String saveStrToTmpFile(String str) throws IOException {
        File tmpFile = File.createTempFile("tmp-", "-file");
        String path = tmpFile.getPath();
        saveStrToFile(str, path);
        tmpFile.deleteOnExit();
        return path;
    }

    public static String getFileStrFromPath(String str) throws IOException {
        FileInputStream fis = new FileInputStream(str);
        return readFromInputStream(fis);
    }

    public static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static void FileByteWriteToFile(byte[] array, String path) {
        try {
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(array);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static String FileByteWriteToTmpFile(byte[] array) throws IOException {
        File tmpFile = File.createTempFile("file-", "-file");
        String path = tmpFile.getPath();
        FileByteWriteToFile(array, path);
        return path;
    }
    public static String getFileExtension(String filePath) {
        Path path = Paths.get(filePath);

        if (path.getFileName() != null) {
            String fileName = path.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1);
            }
        }

        return "";
    }

    public static String getFileNameWithoutExtension(String filePath) {
        Path path = Paths.get(filePath);

        if (path.getFileName() != null) {
            String fileName = path.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                return fileName.substring(0, dotIndex);
            }
        }

        return "";
    }

    public static String getFilePath(String directory, String fileName, String extension) {
        if (directory == null || fileName == null || extension == null) {
            throw new IllegalArgumentException("Directory, file name, and extension cannot be null.");
        }

        if (directory.isEmpty() || fileName.isEmpty() || extension.isEmpty()) {
            throw new IllegalArgumentException("Directory, file name, and extension cannot be empty.");
        }

        // Ensure directory ends with a file separator
        if (!directory.endsWith(java.io.File.separator)) {
            directory += java.io.File.separator;
        }

        return directory + fileName + "." + extension;
    }
}
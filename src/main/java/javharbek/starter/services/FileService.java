package javharbek.starter.services;

import javharbek.starter.config.CdnProperties;
import javharbek.starter.dto.files.FileDTO;
import javharbek.starter.dto.files.ServerCdnDTO;
import javharbek.starter.dto.files.UploadedDTO;
import javharbek.starter.exceptions.CdnServerNotFoundException;
import javharbek.starter.helpers.FileHelper;
import javharbek.starter.repositories.core.FileRepository;
import javharbek.starter.utils.CryptoUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.bouncycastle.crypto.CryptoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
@Setter
@Getter
public class FileService {

    @Autowired
    CdnProperties cdnProperties;
    @Autowired
    FileRepository fileRepository;
    @Value("${security.aes-key}")
    private String aesKey;
    private MultipartFile multipartFile;
    private ServerCdnDTO serverCdnDTO;
    private String filename;
    private String uploadPath;
    private String uploadDir;
    private String uid;

    @SneakyThrows
    public FileDTO upload(MultipartFile file) {
        UploadedDTO dto = transferToStorage(file);
        javharbek.starter.entities.core.File save = save(dto);
        return toDTO(save);
    }

    @SneakyThrows
    public FileDTO upload(MultipartFile file, String hostAlias) {
        UploadedDTO dto = transferToStorage(file, hostAlias);
        javharbek.starter.entities.core.File save = save(dto);
        return toDTO(save);
    }

    private SSHClient setupSshj(String alias) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerByAlias(alias);
        return createSshj(server.getHost(), server.getUsername(), server.getPassword(), server.getPort());
    }

    public ServerCdnDTO getServerByAlias(String alias) throws CdnServerNotFoundException {
        if (!cdnProperties.getServers().containsKey(alias)) {
            throw new CdnServerNotFoundException("Not Found Cdn");
        }
        System.out.println(Objects.toString(cdnProperties.getServers().get(alias)));
        return cdnProperties.getServers().get(alias);
    }

    SSHClient createSshj(String host, String username, String password) throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(host);
        client.authPassword(username, password);
        return client;
    }

    SSHClient createSshj(String host, String username, String password, int port) throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(host, port);
        client.authPassword(username, password);
        return client;
    }

    public UploadedDTO transfer(String localFile, String remoteFile) throws IOException, CdnServerNotFoundException {
        return transfer(localFile, remoteFile, getServerUpload().getAlias());
    }

    public UploadedDTO transfer(String localFile, String remoteFile, ServerCdnDTO serverCdnDTO) throws IOException, CdnServerNotFoundException {
        return transfer(localFile, remoteFile, serverCdnDTO.getAlias());
    }


    public UploadedDTO transfer(MultipartFile multipartFile, String remoteFile) throws IOException, CdnServerNotFoundException {
        return transfer(multipartFile, remoteFile, getServerUpload().getAlias());
    }


    public UploadedDTO transfer(MultipartFile multipartFile, String remoteFile, String hostAlias) throws IOException, CdnServerNotFoundException {
        File tmpFile = File.createTempFile("tmp-", "-tmp");
        multipartFile.transferTo(tmpFile);
        String path = tmpFile.getPath();
        tmpFile.deleteOnExit();
        return transfer(path, remoteFile, hostAlias);
    }

    public UploadedDTO transfer(String localFile, String remoteFile, String hostAlias) throws IOException, CdnServerNotFoundException {
        remoteFile = remoteFile.replace(" ", "-");
        Path path = Paths.get(remoteFile);
        String folders = path.getParent().toString();
        SSHClient sshClient = setupSshj(hostAlias);
        SFTPClient sftpClient = sshClient.newSFTPClient();
        sftpClient.mkdirs(folders.replace("\\", "/"));
        sftpClient.put(localFile, remoteFile);
        sftpClient.close();
        sshClient.disconnect();
        ServerCdnDTO server = getServerByAlias(hostAlias);
        Path filePath = Paths.get(localFile);
        long bytes = Files.size(filePath);
        UploadedDTO uploadedDTO = new UploadedDTO();
        uploadedDTO.setServer(server);
        uploadedDTO.setExtension(FileHelper.getFileExtension(remoteFile));
        uploadedDTO.setName(FileHelper.getFileNameWithoutExtension(remoteFile));
        uploadedDTO.setSize(bytes);
        uploadedDTO.setUploadPath(remoteFile);
        uploadedDTO.setUploadFile(remoteFile.replace(server.getUploadPath(), ""));
        return uploadedDTO;
    }

    public UploadedDTO transferToStorage(MultipartFile multipartFile, String hostAlias) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerByAlias(hostAlias);
        String path = getUploadPath(server, multipartFile);
        return transfer(multipartFile, path);
    }

    public UploadedDTO transferToStorageWithExistPath(String localFile, String originalName, ServerCdnDTO serverCdnDTO) throws IOException, CdnServerNotFoundException {
        originalName = originalName.replace(" ", "-");
        String path = originalName;
        setUploadPath(path);
        return transfer(localFile, path, serverCdnDTO);
    }

    public UploadedDTO transferToStorage(String localFile, String originalName, String hostAlias) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerByAlias(hostAlias);
        String path = getUploadPath(server, originalName);
        return transfer(localFile, path);
    }

    public UploadedDTO transferToStorage(String localFile, String originalName) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerUpload();
        return transferToStorage(localFile, originalName, server.getAlias());
    }

    public UploadedDTO transferToStorage(String localFile, String originalName, ServerCdnDTO server) throws IOException, CdnServerNotFoundException {
        return transferToStorage(localFile, originalName, server.getAlias());
    }

    public UploadedDTO transferToStorage(MultipartFile multipartFile) throws IOException, CdnServerNotFoundException {
        ServerCdnDTO server = getServerUpload();
        return transferToStorage(multipartFile, server.getAlias());
    }

    public UploadedDTO transferToStorage(byte[] imageBytes, String originalName, String hostAlias) throws IOException, CdnServerNotFoundException {
        String path = FileHelper.FileByteWriteToTmpFile(imageBytes);
        return transferToStorage(path, originalName, hostAlias);
    }

    public UploadedDTO transferToStorage(byte[] imageBytes, String originalName) throws IOException, CdnServerNotFoundException {
        String path = FileHelper.FileByteWriteToTmpFile(imageBytes);
        return transferToStorage(path, originalName);
    }

    public String getUploadPath(ServerCdnDTO serverCdnDTO, MultipartFile multipartFile) {
        String path = uploadDir(serverCdnDTO) + getNewUid() + "-" + multipartFile.getOriginalFilename();
        setUploadPath(path);
        return path;
    }

    public String getUploadPath(ServerCdnDTO serverCdnDTO, String originalName) {
        originalName = originalName.replace(" ", "-");
        String path = uploadDir(serverCdnDTO) + getNewUid() + "-" + originalName;
        setUploadPath(path);
        return path;
    }

    public String getUploadPath(ServerCdnDTO serverCdnDTO, String originalName, String newUid) {
        originalName = originalName.replace(" ", "-");
        String path = uploadDir(serverCdnDTO) + newUid + "-" + originalName;
        setUploadPath(path);
        return path;
    }

    public String getNewUid() {
        String uid = UUID.randomUUID().toString();
        setUid(uid);
        return uid;
    }

    public String uploadDir(ServerCdnDTO serverCdnDTO) {
        LocalDate currentdate = LocalDate.now();
        String year = String.valueOf(currentdate.getYear());
        String month = String.valueOf(currentdate.getMonthValue());
        String day = String.valueOf(currentdate.getDayOfMonth());
        String dir = serverCdnDTO.getUploadPath() + "/" + year + "/" + month + "/" + day + "/";
        setUploadDir(dir);
        return dir;
    }

    public ServerCdnDTO getServerUpload() throws CdnServerNotFoundException {
        ArrayList<String> inRotation = cdnProperties.getInRotation();
        Random rand = new Random();
        String alias = inRotation.get(rand.nextInt(inRotation.size()));
        ServerCdnDTO serverCdnDTO = getServerByAlias(alias);
        setServerCdnDTO(serverCdnDTO);
        return serverCdnDTO;
    }

    public javharbek.starter.entities.core.File save(UploadedDTO uploadedDTO) {
        javharbek.starter.entities.core.File file = new javharbek.starter.entities.core.File();
        file.setTitle(uploadedDTO.getName());
        file.setDescription(uploadedDTO.getName());
        file.setSize(uploadedDTO.getSize());
        file.setFile(uploadedDTO.getUploadFile());
        file.setExtension(uploadedDTO.getExtension());
        file.setStatus(1);
        file.setIsDeleted(false);
        file.setHost(uploadedDTO.getServer().getAlias());
        return fileRepository.save(file);
    }

    public String getAbsoluteUrl(javharbek.starter.entities.core.File file) throws CdnServerNotFoundException {
        ServerCdnDTO serverCdnDTO = getServerByAlias(file.getHost());
        return serverCdnDTO.getPublicPath() + file.getFile();
    }


    public String getAbsoluteUrlHttp(javharbek.starter.entities.core.File file) throws CdnServerNotFoundException {
        ServerCdnDTO serverCdnDTO = getServerByAlias(file.getHost());
        return serverCdnDTO.getHttpHost() + file.getFile();
    }

    public String getAbsoluteUrlGlobal(javharbek.starter.entities.core.File file) throws CdnServerNotFoundException {
        ServerCdnDTO serverCdnDTO = getServerByAlias(file.getHost());
        return serverCdnDTO.getPublicGlobalPath() + file.getFile();
    }

    public void removeFile(String filePath, String hostAlias) throws CdnServerNotFoundException, IOException {
        SSHClient sshClient = setupSshj(hostAlias);
        SFTPClient sftpClient = sshClient.newSFTPClient();
        sftpClient.rm(filePath);
    }

    public void removeFile(javharbek.starter.entities.core.File file) throws CdnServerNotFoundException, IOException {
        ServerCdnDTO serverCdnDTO = getServerByAlias(file.getHost());
        SSHClient sshClient = setupSshj(file.getHost());
        SFTPClient sftpClient = sshClient.newSFTPClient();
        String removeFilePath = serverCdnDTO.getUploadPath() + file.getFile();
        try {
            sftpClient.rm(removeFilePath);
        } catch (Exception exception) {

        }

        fileRepository.delete(file);
    }

    public void enc(String inputFilePath, String outputFilePath) {
        String key = aesKey;
        File inputFile = new File(inputFilePath);
        File encryptedFile = new File(outputFilePath);

        try {
            CryptoUtils.encrypt(key, inputFile, encryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void dec(String inputFilePath, String outputFilePath) {
        String key = aesKey;
        File encryptedFile = new File(inputFilePath);
        File decryptedFile = new File(outputFilePath);

        try {
            CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            ex.printStackTrace();
        }
    }

    public String dec(javharbek.starter.entities.core.File inputFile) throws CdnServerNotFoundException, IOException {
        String tmpFile = FileHelper.downloadFileToTmp(getAbsoluteUrlHttp(inputFile));
        String outputTmpFile = FileHelper.saveStrToTmpFile("");
        dec(tmpFile, outputTmpFile);
        return outputTmpFile;
    }

    public String decToStr(javharbek.starter.entities.core.File inputFile) throws CdnServerNotFoundException, IOException {
        String path = dec(inputFile);
        return Files.readString(Path.of(path));
    }

    public javharbek.starter.entities.core.File setType(javharbek.starter.entities.core.File file, String type, String typeId, String typeFor) {
        file.setType(type);
        file.setTypeId(typeId);
        file.setTypeFor(typeFor);
        return fileRepository.save(file);
    }

    public javharbek.starter.entities.core.File getById(String id) {
        return fileRepository.findById(id).orElseThrow();
    }

    @SneakyThrows
    public FileDTO toDTO(javharbek.starter.entities.core.File file) {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setExtension(file.getExtension());
        fileDTO.setUrl(getAbsoluteUrl(file));
        fileDTO.setId(file.getId());
        fileDTO.setTitle(file.getTitle());
        return fileDTO;
    }

    public FileDTO toDTOGlobal(javharbek.starter.entities.core.File file) throws CdnServerNotFoundException {
        FileDTO fileDTO = new FileDTO();
        fileDTO.setExtension(file.getExtension());
        fileDTO.setUrl(getAbsoluteUrlGlobal(file));
        fileDTO.setId(file.getId());
        fileDTO.setTitle(file.getTitle());
        return fileDTO;
    }

    public javharbek.starter.entities.core.File getOne(String id){
        return fileRepository.findById(id).orElseThrow();
    }

}

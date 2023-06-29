package javharbek.starter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import javharbek.starter.dto.files.FileDTO;
import javharbek.starter.dto.files.FileResponseDTO;
import javharbek.starter.dto.files.UploadedDTO;
import javharbek.starter.entities.core.File;
import javharbek.starter.exceptions.AppException;
import javharbek.starter.repositories.core.UserRepository;
import javharbek.starter.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Consumes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequestMapping("/v1/files")
public class FilesController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PostMapping("/upload")
    @Consumes("multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public FileResponseDTO upload(@RequestPart("file") MultipartFile file, String title) throws AppException, IOException {
        UploadedDTO dto = fileService.transferToStorage(file);
        if (title != null) {
            dto.setName(title);
        }
        File uploadFile = fileService.save(dto);
        FileResponseDTO fileResponseDTO = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule())
                .convertValue(uploadFile, FileResponseDTO.class);
        fileResponseDTO.setAbsoluteUrl(fileService.getAbsoluteUrl(uploadFile));
        return fileResponseDTO;
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PostMapping("/upload-files")
    @Consumes("multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public List<FileResponseDTO> uploadFiles(@RequestParam("file") MultipartFile[] files) throws AppException, IOException {
        List<FileResponseDTO> dtoFiles = new ArrayList<>();
        if (files == null) {
            throw new AppException("Files not found");
        }
        for (MultipartFile file : files) {
            UploadedDTO dto = fileService.transferToStorage(file);
            File uploadFile = fileService.save(dto);
            FileResponseDTO fileResponseDTO = new ObjectMapper()
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .registerModule(new JavaTimeModule())
                    .convertValue(uploadFile, FileResponseDTO.class);
            fileResponseDTO.setAbsoluteUrl(fileService.getAbsoluteUrl(uploadFile));
            dtoFiles.add(fileResponseDTO);
        }
        return dtoFiles;

    }


    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public FileDTO getOne(@PathVariable("id") String id) throws AppException, IOException {
        return fileService.toDTO(fileService.getById(id));
    }


}

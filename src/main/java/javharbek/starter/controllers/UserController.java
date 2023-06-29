package javharbek.starter.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import javharbek.starter.config.jwt.JwtUtils;
import javharbek.starter.dto.user.*;
import javharbek.starter.exceptions.AppException;
import javharbek.starter.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/v1/oauth/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public GetTokenDTO login(@Valid OauthLoginDTO dto) throws IOException, AppException {
        return userService.login(dto.toLoginDTO());
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.OK)
    public AuthTokenDTO register(@Valid @RequestBody RegisterDTO dto) throws IOException, AppException {
        return userService.register(dto);
    }

    @PostMapping("registerConfirm")
    @ResponseStatus(HttpStatus.OK)
    public ConfirmedWithTokenDTO registerConfirm(@Valid @RequestBody TokenConfirmDTO dto) throws IOException, AppException {
        return userService.tokenConfirm(dto);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @GetMapping("/me")
    public MeDTO me() throws IOException, AppException {
        String tokenValue = httpServletRequest.getHeader("Authorization").replace("Bearer ", "").trim();
        return userService.getMe(tokenValue);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "jwtToken")})
    @PostMapping("/logout")
    public GetTokenDTO logout() throws IOException {
        String tokenValue = httpServletRequest.getHeader("Authorization").replace("Bearer ", "").trim();
        return userService.logout(tokenValue);
    }


}

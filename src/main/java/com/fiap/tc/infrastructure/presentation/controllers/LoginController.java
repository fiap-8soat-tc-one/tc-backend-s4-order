package com.fiap.tc.infrastructure.presentation.controllers;

import com.fiap.tc.application.usecases.customer.LoadCustomerUseCase;
import com.fiap.tc.infrastructure.core.security.token.CustomerTokenUtil;
import com.fiap.tc.infrastructure.presentation.URLMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(path = URLMapping.ROOT_PUBLIC_API_AUTH)
@Api(tags = "token-endpoint", produces = APPLICATION_JSON_VALUE)
public class LoginController {

    private final LoadCustomerUseCase loadCustomerUseCase;
    private final CustomerTokenUtil jwtUtil;

    public LoginController(LoadCustomerUseCase loadCustomerUseCase, CustomerTokenUtil jwtUtil) {
        this.loadCustomerUseCase = loadCustomerUseCase;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> Login(@ApiParam(value = "Document number for login", required = true) @RequestBody Map<String, String> request) {

        String document = request.get("document");
        Map<String, String> response = new HashMap<>();

        if (document == null || document.isEmpty()) { // anonymous flow
            response.put("access-token", "");
            return ok(response);
        }

        String token = jwtUtil.generateToken(loadCustomerUseCase.load(document));

        response.put("access-token", token);

        return ok(response);
    }


    @PostMapping(path = "/validate", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> ValidateToken(@ApiParam(value = "JWT Token", required = true) @RequestBody Map<String, String> request) {

        String token = request.get("access-token");

        if (token == null || token.isEmpty()) {
            throw new UnauthorizedUserException(format("Invalid JWT Token: %s", token));
        }

        jwtUtil.validateToken(token);
        return ok(null);
    }
}
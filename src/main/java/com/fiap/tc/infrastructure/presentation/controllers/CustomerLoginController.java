package com.fiap.tc.infrastructure.presentation.controllers;

import com.fiap.tc.application.usecases.customer.LoadCustomerUseCase;
import com.fiap.tc.domain.entities.Customer;
import com.fiap.tc.infrastructure.core.security.token.CustomerTokenUtil;
import com.fiap.tc.infrastructure.presentation.URLMapping;
import com.fiap.tc.infrastructure.presentation.requests.CustomerLoginRequest;
import com.fiap.tc.infrastructure.presentation.requests.ValidateCustomerRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(path = URLMapping.ROOT_PUBLIC_API_AUTH)
@Api(tags = "token-endpoint", produces = APPLICATION_JSON_VALUE)
public class CustomerLoginController {

    @Autowired
    private CustomerTokenUtil jwtUtil;

    private final LoadCustomerUseCase loadCustomerUseCase;

    public CustomerLoginController(LoadCustomerUseCase loadCustomerUseCase) {
        this.loadCustomerUseCase = loadCustomerUseCase;
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> Login(@ApiParam(value = "Document number for login", required = true) @RequestBody CustomerLoginRequest request) {

        Map<String, String> response = new HashMap<>();
        String document = request.getDocument();

        if (document == null || document.isEmpty()) { // anonymous flow
            response.put("access_token", "");
            response.put("profile", "anonymous");
            return ok(response);
        }

        Customer customer = loadCustomerUseCase.load(document);
        String token = jwtUtil.generateToken(customer);
        response.put("access_token", token);
        response.put("profile", "customer");
        return ok(response);
    }


    @PostMapping(path = "/validate", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> ValidateToken(@ApiParam(value = "JWT Token", required = true) @RequestBody ValidateCustomerRequest request) {
        String token = request.getAccessToken();
        jwtUtil.validateToken(token);
        return ok(null);
    }
}
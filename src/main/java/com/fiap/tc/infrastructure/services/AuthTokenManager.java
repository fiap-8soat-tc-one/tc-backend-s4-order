package com.fiap.tc.infrastructure.services;

import com.fiap.tc.infrastructure.cache.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenManager {
    private final AuthLoginClient authLoginClient;
    private final AuthRepository authRepository;
    private final String userName;

    public AuthTokenManager(AuthLoginClient authLoginClient, AuthRepository authRepository,
                            @Value("${app.rest.client.oauth.userName}") String userName) {
        this.authLoginClient = authLoginClient;
        this.authRepository = authRepository;
        this.userName = userName;
    }

    public String getToken() {
        var authTokenOptional = authRepository.getToken(userName);
        if (authTokenOptional.isEmpty()) {
            var authDetail = authLoginClient.execute();
            authRepository.saveToken(userName, authDetail.getAccessToken());
            return authDetail.getAccessToken();
        }
        return authTokenOptional.get();
    }

}

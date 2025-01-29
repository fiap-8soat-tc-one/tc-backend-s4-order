package com.fiap.tc.infrastructure.services;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.tc.fixture.FixtureTest;
import com.fiap.tc.infrastructure.cache.AuthRepository;
import com.fiap.tc.infrastructure.dto.AuthDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthTokenManagerTest extends FixtureTest {

    @Mock
    private AuthLoginClient authLoginClientMock;
    @Mock
    private AuthRepository authRepositoryMock;
    private String username = "user-test";

    private AuthTokenManager authTokenManager;

    private AuthDetail authDetail;
    private String token;

    @BeforeEach
    public void setUp() {
        // Arrange
        token = "token-test";
        authDetail = Fixture.from(AuthDetail.class).gimme("valid");
        when(authRepositoryMock.getToken(anyString())).thenReturn(Optional.of(token));

        authTokenManager = new AuthTokenManager(authLoginClientMock, authRepositoryMock, username);
    }

    @Test
    public void getTokenTest() {

        // Act
        var tokenResult = authTokenManager.getToken();

        // Assertions
        assertEquals(token, tokenResult);
        verify(authRepositoryMock, times(1)).getToken(anyString());
    }

    @Test
    public void getToken_WhenEmpty_StoreCacheTest() {

        // Arrange
        when(authRepositoryMock.getToken(anyString())).thenReturn(Optional.empty());
        when(authLoginClientMock.execute()).thenReturn(authDetail);

        // Act
        var tokenResult = authTokenManager.getToken();

        //Assertions
        assertEquals(token, tokenResult);
        verify(authRepositoryMock, times(1)).getToken(anyString());
        verify(authRepositoryMock, times(1)).saveToken(anyString(), anyString());
        verify(authLoginClientMock, times(1)).execute();

    }

}
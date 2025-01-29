package com.fiap.tc.infrastructure.services;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.tc.fixture.FixtureTest;
import com.fiap.tc.infrastructure.core.config.RestClientOAuthConfig;
import com.fiap.tc.infrastructure.dto.AuthDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthLoginClientTest extends FixtureTest {

    @Mock
    private WebClient webClientMock;
    @Mock
    private RestClientOAuthConfig restClientOAuthConfigMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    private AuthDetail authDetail;

    @InjectMocks
    private AuthLoginClient authLoginClient;

    @BeforeEach
    public void setUp() {
        when(restClientOAuthConfigMock.getClientId()).thenReturn("client-id");
        when(restClientOAuthConfigMock.getRetry()).thenReturn(2);
        when(restClientOAuthConfigMock.getTimeout()).thenReturn(5);
        when(restClientOAuthConfigMock.getUserName()).thenReturn("user-test");
        when(restClientOAuthConfigMock.getPassword()).thenReturn("test");

        authDetail = Fixture.from(AuthDetail.class).gimme("valid");
    }

    @Test
    public void executeLoginTest() {

        // Arrange
        when(webClientMock.method(Mockito.any(HttpMethod.class))).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(any(String.class))).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.headers(any())).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.contentType(any())).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.bodyValue(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AuthDetail.class)).thenReturn(Mono.just(authDetail));

        // Act
        var authResult = authLoginClient.execute();

        // Assert
        assertEquals(authDetail, authResult);
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(requestBodyUriSpecMock).headers(headersCaptor.capture());

        Consumer<HttpHeaders> consumer = headersCaptor.getValue();
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        assert headers.get("Authorization").contains("Basic client-id");
    }


}
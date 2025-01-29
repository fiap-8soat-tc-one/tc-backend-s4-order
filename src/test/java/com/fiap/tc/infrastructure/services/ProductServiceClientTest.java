package com.fiap.tc.infrastructure.services;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.tc.domain.entities.Product;
import com.fiap.tc.fixture.FixtureTest;
import com.fiap.tc.infrastructure.core.config.RestClientBackofficeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceClientTest extends FixtureTest {

    @Mock
    private WebClient webClientBackoffice;
    @Mock
    private RestClientBackofficeConfig restClientConfigMock;
    @Mock
    private AuthTokenManager authTokenManager;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @InjectMocks
    private ProductServiceClient productServiceClient;


    private String authToken;

    private Product product;

    private UUID productId;


    @BeforeEach
    public void setUp() {
        productId = UUID.randomUUID();
        product = Fixture.from(Product.class).gimme("valid");
        when(restClientConfigMock.getRetry()).thenReturn(2);
        when(restClientConfigMock.getTimeout()).thenReturn(5);
        authToken = "mockAuthToken";
    }

    @Test
    void shouldSetCorrectHeadersWhenCallingLoad() {
        // Arrange
        when(authTokenManager.getToken()).thenReturn(authToken);
        when(webClientBackoffice.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(String.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Product.class)).thenReturn(Mono.just(product));

        // Act
        productServiceClient.load(productId);

        // Assert
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(requestBodyUriSpec).headers(headersCaptor.capture());

        Consumer<HttpHeaders> consumer = headersCaptor.getValue();
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        assert headers.get("Authorization").contains("Bearer " + authToken);
        assert headers.get("Accept").contains(MediaType.APPLICATION_JSON_VALUE);

    }

}

package com.fiap.tc.infrastructure.services;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.tc.domain.entities.Product;
import com.fiap.tc.fixture.FixtureTest;
import com.fiap.tc.infrastructure.core.config.RestClientBackofficeConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceClientTest extends FixtureTest {

    @Mock
    private WebClient webClientMock;
    @Mock
    private RestClientBackofficeConfig restClientConfigMock;
    @Mock
    private AuthTokenManager authTokenManagerMock;

    @InjectMocks
    private ProductServiceClient productServiceClient;

    private Product product;

    private UUID productId;

    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);

    private WebClient.RequestHeadersSpec requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);

    private WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

    @BeforeEach
    public void setUp() {
        productId = UUID.randomUUID();
        product = Fixture.from(Product.class).gimme("valid");
        when(restClientConfigMock.getRetry()).thenReturn(2);
        when(restClientConfigMock.getTimeout()).thenReturn(5);

    }

    @Test
    public void testLoad_ShouldReturnOptionalOfProduct_WhenProductIsFound() {

        Mono<Product> productMono = Mono.just(product);

        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(String.format("/api/private/v1/products/%s", productId))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.headers(Mockito.any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Product.class)).thenReturn(productMono);


        Optional<Product> productOptional = productServiceClient.load(productId);
        Assertions.assertEquals(product, productOptional.get());
    }

}

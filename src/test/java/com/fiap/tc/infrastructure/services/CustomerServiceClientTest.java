package com.fiap.tc.infrastructure.services;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.tc.domain.entities.Customer;
import com.fiap.tc.fixture.FixtureTest;
import com.fiap.tc.infrastructure.core.config.RestClientBackofficeConfig;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceClientTest extends FixtureTest {

    @Mock
    private WebClient webClientMock;
    @Mock
    private RestClientBackofficeConfig restClientBackofficeConfigMock;
    @Mock
    private AuthTokenManager authTokenManagerMock;

    @InjectMocks
    private CustomerServiceClient customerServiceClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;
    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    private Customer customer;
    private String document;

    @BeforeEach
    public void setUp() {
        document = "11111111111";
        customer = Fixture.from(Customer.class).gimme("valid");
        when(restClientBackofficeConfigMock.getRetry()).thenReturn(3);
        when(restClientBackofficeConfigMock.getTimeout()).thenReturn(5);
        when(authTokenManagerMock.getToken()).thenReturn("token");

    }

    @Test
    public void loadCustomerTest() {
        // Arrange
        when(webClientMock.method(Mockito.
                any(HttpMethod.class))).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(any(String.class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.headers(any())).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Customer.class)).thenReturn(Mono.just(customer));

        // Act
        var customerResult = customerServiceClient.load(document);

        //Assert
        assertEquals(customer, customerResult.get());

        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(requestBodySpecMock).headers(headersCaptor.capture());

        Consumer<HttpHeaders> consumer = headersCaptor.getValue();
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        assert headers.get("Authorization").contains("Bearer token");
    }
}
package com.fiap.tc.infrastructure.presentation.controllers;

import com.fiap.tc.application.usecases.order.ListOrdersUseCase;
import com.fiap.tc.application.usecases.order.LoadOrderUseCase;
import com.fiap.tc.application.usecases.order.RegisterOrderUseCase;
import com.fiap.tc.application.usecases.order.UpdateStatusOrderUseCase;
import com.fiap.tc.infrastructure.core.security.token.CustomerTokenUtil;
import com.fiap.tc.infrastructure.presentation.URLMapping;
import com.fiap.tc.infrastructure.presentation.requests.OrderRequest;
import com.fiap.tc.infrastructure.presentation.requests.OrderStatusRequest;
import com.fiap.tc.infrastructure.presentation.response.DefaultResponse;
import com.fiap.tc.infrastructure.presentation.response.OrderListResponse;
import com.fiap.tc.infrastructure.presentation.response.OrderResponse;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static com.fiap.tc.infrastructure.presentation.mappers.base.MapperConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping
@Api(tags = "Orders API V1", produces = APPLICATION_JSON_VALUE)
@Slf4j
public class OrderController {

    @Autowired
    private CustomerTokenUtil jwtUtil;

    private final RegisterOrderUseCase registerOrderUseCase;
    private final LoadOrderUseCase loadOrderUseCase;
    private final UpdateStatusOrderUseCase updateStatusOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;

    public OrderController(RegisterOrderUseCase registerOrderUseCase,
                           LoadOrderUseCase loadOrderUseCase,
                           UpdateStatusOrderUseCase updateStatusOrderUseCase,
                           ListOrdersUseCase listOrdersUseCase) {
        this.registerOrderUseCase = registerOrderUseCase;
        this.loadOrderUseCase = loadOrderUseCase;
        this.updateStatusOrderUseCase = updateStatusOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
    }

    @ApiOperation(value = "Find Order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Find Order", response = OrderResponse.class)
    })
    @GetMapping(path = URLMapping.ROOT_PRIVATE_API_ORDERS + "/{id}")
    @PreAuthorize("hasAuthority('SEARCH_ORDERS')")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID id) {
        return ok(ORDER_RESPONSE_MAPPER.fromDomain(loadOrderUseCase.load(id)));
    }

    @ApiOperation(value = "create order", notes = "(Public Endpoint) This endpoint is responsible for creating the order, receiving the product identifiers and their quantities.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered order", response = OrderResponse.class),
    })
    @PostMapping(path = URLMapping.ROOT_PUBLIC_API_ORDERS, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> register(
            @RequestHeader(value = "X-Authorization-Token", required = false) String accessToken,
            @ApiParam(value = "Order details for creating a new order",
                    required = true) @RequestBody @Valid OrderRequest request) {

        try {
            var listOfItems = request.getOrderItems().stream().map(ORDER_ITEM_MAPPER::toDomain).toList();

            // anonymous flow
            if (accessToken == null) {
                return ok(ORDER_RESPONSE_MAPPER.fromDomain(registerOrderUseCase.register(null, listOfItems)));
            }

            var document = jwtUtil.getDocumentFromToken(accessToken);
            return ok(ORDER_RESPONSE_MAPPER.fromDomain(registerOrderUseCase.register(document, listOfItems)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ApiOperation(value = "update order status", notes = "(Private Endpoint) This endpoint is responsible for updating the order status for tracking by both the kitchen and the customer (reflected on the system monitor).")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated order status", response = DefaultResponse.class),
            @ApiResponse(code = 401, message = "You are not authorized to perform this action"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
    })
    @PutMapping(path = URLMapping.ROOT_PRIVATE_API_ORDERS + "/status", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('UPDATE_STATUS_ORDERS')")
    public ResponseEntity<DefaultResponse> updateStatus(@ApiParam(value = "Order status update details", required = true) @RequestBody @Valid OrderStatusRequest request) {
        try {
            updateStatusOrderUseCase.update(request.getId(), request.getStatus());
            return ok(new DefaultResponse());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @ApiOperation(value = "list of orders", notes = "(Private Endpoint) This endpoint is responsible for listing all orders.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of orders", response = OrderListResponse.class, responseContainer = "Page"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
    })
    @GetMapping(path = URLMapping.ROOT_PRIVATE_API_ORDERS)
    @PreAuthorize("hasAuthority('LIST_ORDERS')")
    public ResponseEntity<Page<OrderListResponse>> list(@ApiParam(required = true, value = "Orders Pagination") Pageable pageable) {
        try {
            return ok(listOrdersUseCase.list(pageable).map(ORDER_LIST_MAPPER::fromDomain));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

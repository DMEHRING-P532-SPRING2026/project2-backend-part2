package iu.devinmehringer.project2.controller;

import iu.devinmehringer.project2.controller.dto.CommandResponse;
import iu.devinmehringer.project2.controller.dto.OrderRequest;
import iu.devinmehringer.project2.controller.dto.OrderResponse;
import iu.devinmehringer.project2.managers.order.OrderManager;
import iu.devinmehringer.project2.managers.order.TriageStrategy;
import iu.devinmehringer.project2.managers.order.TriageStrategyType;
import iu.devinmehringer.project2.model.command.CommandRecord;
import iu.devinmehringer.project2.model.order.Order;
import iu.devinmehringer.project2.model.order.Type;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderManager orderManager;

    private static class OrderMapper{
        public static OrderResponse toDTO(Order order) {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setId(order.getId());
            orderResponse.setType(order.getType());
            orderResponse.setPatient(order.getPatient());
            orderResponse.setClinician(order.getClinician());
            orderResponse.setDescription(order.getDescription());
            orderResponse.setPriority(order.getPriority());
            orderResponse.setStatus(order.getStatus());
            orderResponse.setCreatedAt(order.getCreatedAt());
            orderResponse.setLastModifiedAt(order.getLastModifiedAt());
            orderResponse.setDeadline(order.getDeadline());
            return orderResponse;
        }
    }

    private static class CommandRecordMapper {
        public static CommandResponse toDTO(CommandRecord commandRecord) {
            CommandResponse commandResponse = new CommandResponse();
            commandResponse.setOrderId(commandRecord.getOrderId());
            commandResponse.setActor(commandRecord.getActor());
            commandResponse.setType(commandRecord.getType());
            commandResponse.setExecutedAt(commandRecord.getExecutedAt());
            return commandResponse;
        }
    }

    public OrderController(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderManager.createOrder(orderRequest);
        return ResponseEntity.created(URI.create("/api/orders/" + order.getId()))
                .body(OrderMapper.toDTO(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderManager.getOrderById(id);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<OrderResponse> claimOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        Order order = orderManager.claimOrder(id, orderRequest);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        Order order = orderManager.cancelOrder(id, orderRequest);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<OrderResponse> submitOrder(@PathVariable Long id, @RequestBody OrderRequest orderRequest) {
        Order order = orderManager.submitOrder(id, orderRequest);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getPendingOrders(
            @RequestParam Type type,
            @RequestParam(defaultValue = "PRIORITY_FIRST") TriageStrategyType strategy) {
            List<Order> orders  = orderManager.getPendingOrders(strategy, type);
        return ResponseEntity.ok(orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/commands")
    public ResponseEntity<List<CommandResponse>> getCommands() {
        List<CommandRecord> commands = orderManager.getOrderCommands();
        return ResponseEntity.ok(commands.stream()
                .map(CommandRecordMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("API is running");
    }
}

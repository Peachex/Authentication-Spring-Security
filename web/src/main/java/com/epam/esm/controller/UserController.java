package com.epam.esm.controller;

import com.epam.esm.constant.HeaderName;
import com.epam.esm.constant.ResponseMessageName;
import com.epam.esm.dto.Order;
import com.epam.esm.dto.Tag;
import com.epam.esm.dto.User;
import com.epam.esm.hateoas.Hateoas;
import com.epam.esm.jwt.JwtProvider;
import com.epam.esm.response.EntityOperationResponse;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserService;
import com.epam.esm.util.MessageLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService<User> userService;
    private final OrderService<Order> orderService;
    private final TagService<Tag> tagService;
    private final Hateoas<User> userHateoas;
    private final Hateoas<Order> orderHateoas;
    private final Hateoas<Tag> tagHateoas;
    private final Hateoas<EntityOperationResponse> responseHateoas;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserController(UserService<User> userService, OrderService<Order> orderService, TagService<Tag> tagService,
                          Hateoas<User> userHateoas, Hateoas<Order> orderHateoas, Hateoas<Tag> tagHateoas,
                          @Qualifier("orderOperationResponseHateoas") Hateoas<EntityOperationResponse> responseHateoas,
                          JwtProvider jwtProvider) {
        this.userService = userService;
        this.orderService = orderService;
        this.tagService = tagService;
        this.userHateoas = userHateoas;
        this.orderHateoas = orderHateoas;
        this.tagHateoas = tagHateoas;
        this.responseHateoas = responseHateoas;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable String id) {
        User user = userService.findById(id, jwtProvider.getUserNameFromSecurityContext());
        userHateoas.createHateoas(user);
        return user;
    }

    @GetMapping
    public List<User> findAllUsers(@RequestParam int page, @RequestParam int elements) {
        List<User> users = userService.findAll(page, elements);
        users.forEach(userHateoas::createHateoas);
        return users;
    }

    @PostMapping("/{userId}/orders/new/{certificateId}")
    public EntityOperationResponse createOrder(HttpServletRequest request, @PathVariable String userId,
                                               @PathVariable String certificateId) {
        EntityOperationResponse response = new EntityOperationResponse(EntityOperationResponse.Operation.CREATION,
                ResponseMessageName.ORDER_CREATE_OPERATION, orderService.createOrder(userId, certificateId,
                jwtProvider.getUserName(request.getHeader(HeaderName.AUTHENTICATION_TOKEN))),
                MessageLocale.defineLocale(request.getHeader(HeaderName.LOCALE)));
        responseHateoas.createHateoas(response);
        return response;
    }

    @GetMapping("/{userId}/orders")
    public List<Order> findUserOrders(@PathVariable String userId, @RequestParam int page, @RequestParam int elements) {
        List<Order> orders = orderService.findByUserId(page, elements, userId,
                jwtProvider.getUserNameFromSecurityContext());
        orders.forEach(orderHateoas::createHateoas);
        return orders;
    }

    @GetMapping("/{userId}/orders/{orderId}")
    public Order findUserOrder(@PathVariable String userId, @PathVariable String orderId) {
        Order order = orderService.findByUserIdAndOrderId(userId, orderId,
                jwtProvider.getUserNameFromSecurityContext());
        orderHateoas.createHateoas(order);
        return order;
    }

    @GetMapping("/{userId}/orders/tags/popular")
    public Tag findMostUsedTagOfUserWithHighestCostOfAllOrders(@PathVariable String userId) {
        Tag tag = tagService.findMostUsedTagOfUserWithHighestCostOfAllOrders(userId,
                jwtProvider.getUserNameFromSecurityContext());
        tagHateoas.createHateoas(tag);
        return tag;
    }
}

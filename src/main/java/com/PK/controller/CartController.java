package com.PK.controller;

import com.PK.global.GlobalData;
import com.PK.model.Orders;
import com.PK.model.Product;
import com.PK.service.OrderService;
import com.PK.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;


    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable int id){
        GlobalData.cart.add(productService.getProductById(id).get());
        return "redirect:/shop";
    }

    @GetMapping("/cart")
    public String cartGet(Model model) {
        model.addAttribute("cartCount", GlobalData.cart.size());
        model.addAttribute("total", GlobalData.cart.stream().mapToDouble(Product::getPrice).sum());
        model.addAttribute("cart", GlobalData.cart);
        return "cart";
    }

    @GetMapping("/cart/removeItem/{index}")
    public String cartItemRemove(@PathVariable int index) {
        GlobalData.cart.remove(index);
        return "redirect:/cart";
    }
    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("data",LocalTime.now().plusMinutes(60));
        return "checkout";
    }



    @PostMapping("/cart/placeOrder")
    public String placeOrder(Principal principal) {
        List<Product> cartItems = GlobalData.cart;
        StringBuilder productsBuilder = new StringBuilder();
        double total = GlobalData.cart.stream().mapToDouble(Product::getPrice).sum();

        // Konwersja listy produktów na łańcuch znaków
        for (Product product : cartItems) {
            productsBuilder.append(product.getName()).append(", ");
        }
        String products = productsBuilder.toString();

        // Pobranie nazwy użytkownika zalogowanego użytkownika

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);
        System.out.println(products);
        Orders orders = new Orders();
        orders.setUser(username);
        orders.setProducts(products);
        orders.setTotal(total);
        orders.setOrderDate(LocalDateTime.now());

        orderService.saveOrder(orders);
        // Wyczyść koszyk po złożeniu zamówienia
        GlobalData.cart.clear();

        return "redirect:/checkout";
    }





}

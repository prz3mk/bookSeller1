package com.PK.controller;

import com.PK.global.GlobalData;
import com.PK.model.Orders;
import com.PK.model.Product;
import com.PK.model.User;
import com.PK.repository.UserRepository;
import com.PK.service.CategoryService;
import com.PK.service.OrderService;
import com.PK.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

//    @GetMapping({"/", "/home"})
//    public String home(Model model){
//        model.addAttribute("cartCount", GlobalData.cart.size());
//
//        return "shop";
//    }



    @GetMapping({"/shop/category/{id}"})
    public String shopByCategory(Model model, @PathVariable int id){
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("cartCount", GlobalData.cart.size());
        model.addAttribute("products", productService.getAllProductsByCategoryId(id));
        return "shop";
    }

    @GetMapping({"/shop/viewproduct/{id}"})
    public String viewProduct(Model model, @PathVariable int id){
        model.addAttribute("product", productService.getProductById(id).get());
        model.addAttribute("cartCount", GlobalData.cart.size());
        return "viewProduct";
    }

    @GetMapping({"/shop", "/", "/home"})
    public String shop(Model model, @RequestParam(value = "search", required = false) String search) {
        List<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productService.searchProductsByName(search);
        } else {
            products = productService.getAllProduct();
        }

        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("products", products);
        model.addAttribute("cartCount", GlobalData.cart.size());

        return "shop";
    }

    @GetMapping("/myOrders")
    public String getUserOrders(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Orders> userOrders = orderService.getUserOrders(username);
        model.addAttribute("orders", userOrders);
        return "myOrders";
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("user") User updatedUser, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username);
        if (user != null) {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            String newPassword = updatedUser.getPassword();
            if (!newPassword.isEmpty()) {
                user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            }

            userRepository.save(user);
        }
        return "redirect:/shop";
    }

    @GetMapping("/updateUser")
    public String getUser(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username);
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "userUpdate";
    }



}



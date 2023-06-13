package com.PK.controller;

import com.PK.model.Product;
import com.PK.repository.UserRepository;
import com.PK.service.CategoryService;
import com.PK.service.OrderService;
import com.PK.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testShopByCategory() throws Exception {
        // Przygotowanie danych testowych
        int categoryId = 1; // Identyfikator kategorii do przetestowania

        // Wykonanie żądania GET do /shop/category/{id}
        mockMvc.perform(MockMvcRequestBuilders.get("/shop/category/{id}", categoryId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("cartCount"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"))
                .andExpect(MockMvcResultMatchers.view().name("shop"));
    }



    @Test
    public void testShop() throws Exception {
        // Wykonanie żądania GET do /shop
        mockMvc.perform(MockMvcRequestBuilders.get("/shop"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("categories"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("cartCount"))
                .andExpect(MockMvcResultMatchers.view().name("shop"));
    }


}

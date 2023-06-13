package com.PK.controller;

import com.PK.model.Category;
import com.PK.model.Orders;
import com.PK.repository.OrderRepository;
import com.PK.repository.UserRepository;
import com.PK.service.CategoryService;
import com.PK.service.CustomUserDetailService;
import com.PK.service.OrderService;
import com.PK.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminControllerTest {
    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void adminHome_ShouldReturnAdminHomePage() {
        String expectedView = "adminHome";

        String actualView = adminController.adminHome();

        assertEquals(expectedView, actualView);
    }

    @Test
    void getCat_ShouldReturnCategoriesViewAndAddCategoriesToModel() {
        List<Category> categories = new ArrayList<>();
        when(categoryService.getAllCategory()).thenReturn(categories);
        Model model = mock(Model.class);
        String expectedView = "categories";

        String actualView = adminController.getCat(model);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("categories", categories);
    }

    @Test
    void getCatAdd_ShouldReturnCategoriesAddViewAndAddNewCategoryToModel() {
        Model model = mock(Model.class);
        String expectedView = "categoriesAdd";

        String actualView = adminController.getCatAdd(model);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("category", new Category());
    }

    @Test
    void postCatAdd_ShouldRedirectToCategoriesView() {
        Category category = new Category();
        String expectedView = "redirect:/admin/categories";

        String actualView = adminController.postCatAdd(category);

        assertEquals(expectedView, actualView);
        verify(categoryService).addCategory(category);
    }

    @Test
    void deleteCat_ShouldRedirectToCategoriesView() {
        int categoryId = 1;
        String expectedView = "redirect:/admin/categories";

        String actualView = adminController.deleteCat(categoryId);

        assertEquals(expectedView, actualView);
        verify(categoryService).removeCategoryById(categoryId);
    }

    @Test
    void updateCat_WithExistingCategory_ShouldReturnCategoriesAddViewAndAddCategoryToModel() {
        int categoryId = 1;
        Category category = new Category();
        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.of(category));
        Model model = mock(Model.class);
        String expectedView = "categoriesAdd";

        String actualView = adminController.updateCat(categoryId, model);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("category", category);
    }

    @Test
    void updateCat_WithNonExistingCategory_ShouldReturnNotFoundView() {
        int categoryId = 1;
        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.empty());
        String expectedView = "404";

        String actualView = adminController.updateCat(categoryId, mock(Model.class));

        assertEquals(expectedView, actualView);
    }



    @Test
    void getAllOrders_ShouldReturnAdminOrdersViewAndAddOrdersToModel() {
        List<Orders> orders = new ArrayList<>();
        when(orderService.getAllOrders()).thenReturn(orders);
        Model model = mock(Model.class);
        String expectedView = "adminOrders";

        String actualView = adminController.getAllOrders(model);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("orders", orders);
    }


}

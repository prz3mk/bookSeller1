package com.PK.controller;

import com.PK.dto.ProductDTO;
import com.PK.model.Category;
import com.PK.model.Orders;
import com.PK.model.Product;
import com.PK.model.User;
import com.PK.repository.OrderRepository;
import com.PK.repository.UserRepository;
import com.PK.service.CategoryService;
import com.PK.service.CustomUserDetailService;
import com.PK.service.OrderService;
import com.PK.service.ProductService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Opis klasy.
 */

@Controller
public class AdminController {
    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";

    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;
    @Autowired
    CustomUserDetailService customUserDetailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderRepository orderRepository;

    @GetMapping("/admin")
    public String adminHome(){
        return "adminHome";
    }

    @GetMapping("/admin/categories")
    public String getCat(Model model){
        model.addAttribute("categories", categoryService.getAllCategory());
        return "categories";
    }

    @GetMapping("/admin/categories/add")
    public String getCatAdd(Model model){
        model.addAttribute("category", new Category());
        return "categoriesAdd";
    }

    @PostMapping("/admin/categories/add")
    public String postCatAdd(@ModelAttribute("category") Category category){
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCat(@PathVariable int id) {
        categoryService.removeCategoryById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/update/{id}")
    public String updateCat(@PathVariable int id, Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categoriesAdd";
        } else {
            return "404";
        }
    }


    //product section
    @GetMapping("/admin/products")
    public String products(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "products";
    }

    @GetMapping("/admin/products/add")
    public String productAddGet(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("categories", categoryService.getAllCategory());
        return "productsAdd";
    }

    @PostMapping("/admin/products/add")
    public String productAddPost(@ModelAttribute("productDTO") ProductDTO productDTO,
                                 @RequestParam("productImage") MultipartFile file,
                                 @RequestParam("imgName") String imgName) throws IOException {

        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setAuthor(productDTO.getAuthor()); // Dodanie autora
        product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        String imageUUID;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }
        product.setImageName(imageUUID);
        productService.addProduct(product);

        return "redirect:/admin/products";
    }


    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable long id){
        productService.removeProductById(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/product/update/{id}")
    public String updateProductGet(@PathVariable long id, Model model){
        Product product = productService.getProductById(id).get();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setAuthor(product.getAuthor());
        productDTO.setCategoryId((product.getCategory().getId()));
        productDTO.setPrice(product.getPrice());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageName(product.getImageName());

        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("productDTO", productDTO);

        return "productsAdd";
    }

    @GetMapping("/admin/allOrders")
    public String getAllOrders(Model model) {
        List<Orders> ordersList = orderService.getAllOrders();
        model.addAttribute("orders", ordersList);
        return "adminOrders";
    }

    @GetMapping("/orders/pdf")
    public void generateOrdersPdf(HttpServletResponse response) throws IOException, DocumentException {
        // Pobierz dane zamówień z serwisu lub repozytorium
        List<Orders> orders = orderService.getAllOrders();

        // Utwórz nowy dokument PDF
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        // Otwórz dokument
        document.open();

        // Dodaj zawartość do dokumentu
        for (Orders order : orders) {
            Paragraph paragraph = new Paragraph("Nr: " + order.getId() + "\n" +
                    "Użytkownik: " + order.getUser() + "\n" +
                    "Produkty: " + order.getProducts() + "\n" +
                    "Całkowita wartość: " + order.getTotal() + "\n" +
                    "Data zamówienia: " + order.getOrderDate() + "\n\n");
            document.add(paragraph);
        }

        // Zamknij dokument
        document.close();

        // Ustaw nagłówki odpowiedzi HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=orders.pdf");

        // Wyślij zawartość PDF jako odpowiedź
        response.flushBuffer();
    }


    @GetMapping("/admin/allClients")
    public String getAllClients(Model model) {
        List<User> clients = customUserDetailService.getAllClients();
        model.addAttribute("clients", clients);
        return "allClients";
    }

    @GetMapping("/admin/deleteClient/{id}")
    public String deleteClient(@PathVariable int id) {
        customUserDetailService.removeUserById(id);
        return "redirect:/admin/allClients";
    }


    @GetMapping("/admin/client/{email}")
    public String getUserOrders(@PathVariable String email, Model model) {

        if (email != null) {
            List<Orders> orders = orderRepository.findByUser(email);
            model.addAttribute("orders", orders);
            model.addAttribute("nazwaUzy", email);
            return "userEmailOrders";
        }
        return "userEmailOrders";
    }

}




























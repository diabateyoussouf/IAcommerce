package net.youssouf.backend.controllers;

import net.youssouf.backend.entities.Category;
import net.youssouf.backend.entities.Product;
import net.youssouf.backend.repositories.CategoryRepository;
import net.youssouf.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "${app.frontend.url}")
public class RestCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/images")
    public  List<Category> findAllWithImage(){
        return categoryRepository.findAll();
    }

}

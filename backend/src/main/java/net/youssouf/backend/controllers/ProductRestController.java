package net.youssouf.backend.controllers;

import net.youssouf.backend.dtos.ProductDTO;
import net.youssouf.backend.entities.Product;
import net.youssouf.backend.mappers.ProductMapper;
import net.youssouf.backend.repositories.ProductRepository;
import net.youssouf.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/products")
// eviter cross token
@CrossOrigin(origins = "${app.frontend.url}")
public class ProductRestController {
    @Autowired
    private final ProductService productService;
    @Autowired
    private final ProductMapper productMapper;
    // automatic injection
    @Autowired
    private ProductRepository productRepository;

    public ProductRestController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    // all product controller with filter
    @GetMapping("/all-products")
    public Page<Product> getAllProducts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "") String sort
    ) {
        Pageable pageable;

        switch (sort) {
            case "priceLowHigh":
                pageable = PageRequest.of(page, size, Sort.by("sellPrice").ascending());
                break;
            case "priceHighLow":
                pageable = PageRequest.of(page, size, Sort.by("sellPrice").descending());
                break;
            case "rating":
                pageable = PageRequest.of(page, size, Sort.by("rating").descending());
                break;
            default:
                pageable = PageRequest.of(page, size);
        }

        if (!search.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(search, pageable);
        }

        return productRepository.findAll(pageable);
    }


    // controller turn all products
    @GetMapping("/trending")
    public List<Product> findTrendingProducts() {
        return productRepository.findTrendingProducts();
    }

    // marked liked or disliked
    @PostMapping("/{productId}/toggle-like")
    public ResponseEntity<Product> toggleLike(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Inverse la valeur de hasLiked
        product.setHasLiked(!product.isHasLiked());

        // Mets à jour le nombre de likes en fonction
        if (product.isHasLiked()) {
            product.setNumberOfLiked(product.getNumberOfLiked() + 1);
        } else {
            product.setNumberOfLiked(Math.max(0, product.getNumberOfLiked() - 1));
        }

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }
    // 3 latest product
    @GetMapping("/latest")
    public List<Product> findLatestProducts() {
        return productRepository.findTop3ByOrderByDateDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        // increment number of views( à ameliorer)
        productService.incrementViewCount(id);
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        ProductDTO dto = productMapper.toDto(product);
        System.out.println(dto);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/similar/{id}")
    public ResponseEntity<List<ProductDTO>> findSimilarProducts(@PathVariable Long id) {
        Product product = productService.findById(id);
        if (product == null) {
            throw new RuntimeException("Product non existent");
        }

        Long categoryId = product.getCategory().getId();
        List<Product> similarProducts = productService.getProductSimilar(categoryId,product.getId());
        List<ProductDTO> dtoList = similarProducts.stream()
                .map(productMapper::toDto)
                .toList();

        return ResponseEntity.ok(dtoList);
    }



}
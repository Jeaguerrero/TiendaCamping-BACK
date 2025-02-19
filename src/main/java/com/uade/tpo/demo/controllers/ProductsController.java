package com.uade.tpo.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.dto.FilterProductRequest;
import com.uade.tpo.demo.entity.dto.ProductRequest;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.service.ProductService;

import java.net.URI;
import java.util.Optional;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("products")
public class ProductsController {

    @Autowired
    private ProductService ProductService;

    @GetMapping
    public ResponseEntity<Page<Product>> getCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(ProductService.getProducts(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(ProductService.getProducts(PageRequest.of(page, size)));
    }

    @GetMapping("/filterByPrice")
    public ResponseEntity<List<Product>> getMethodName(@RequestBody FilterProductRequest filterProductRequest) {
        List<Product> products = ProductService.getProductByPrice(filterProductRequest.getMaxPrice(),
                filterProductRequest.getMinPrice());
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{ProductId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long ProductId) {
        Optional<Product> result = ProductService.getProductById(ProductId);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{Category}")
    public ResponseEntity<List<Product>> getProductByCategory(@PathVariable("Category") String category) {
        List<Product> products = ProductService.getProductByCategory(category);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/name/{Description}")
    public ResponseEntity<List<Product>> getProductByName(@PathVariable("Description") String description) {
        List<Product> products = ProductService.getProductByName(description);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping
    // Crea productos en la base de datos
    public ResponseEntity<Object> createProduct(@RequestBody ProductRequest ProductRequest)
            throws ProductDuplicateException {
        Product result = ProductService.createProduct(
                ProductRequest.getDescription(),
                ProductRequest.getPrice(),
                ProductRequest.getStock(),
                ProductRequest.getCategoryId(),
                ProductRequest.getImageId());
        return ResponseEntity.created(URI.create("/products/" + result.getId())).body(result);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> editProduct(@PathVariable Long productId,
            @RequestBody ProductRequest productRequest) {
        Optional<Product> result = ProductService.editProduct(
                productId,
                productRequest.getDescription(),
                productRequest.getPrice(),
                productRequest.getStock(),
                productRequest.getCategoryId(),
                productRequest.getImageId());

        if (result.isPresent()) {
            return ResponseEntity.ok(result.get()); // Retorna el producto actualizado con estado 200 OK
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 Not Found si el producto no existe
    }

    @DeleteMapping("/{ProductId}")
    public Boolean deleteProductById(@PathVariable Long ProductId) {
        return ProductService.deleteProductById(ProductId);

    }

}
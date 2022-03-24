package edu.lysak.recipes.service;

import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    public Product saveAndGetProduct(String name) {
        return getProductByName(name)
                .orElseGet(() -> addProduct(new Product(name)));
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }


}

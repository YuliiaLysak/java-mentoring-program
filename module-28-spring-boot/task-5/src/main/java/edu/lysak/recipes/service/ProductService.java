package edu.lysak.recipes.service;

import edu.lysak.recipes.dto.IngredientDto;
import edu.lysak.recipes.model.NutritionalValue;
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

    public Product saveAndGetProduct(IngredientDto ingredientDto) {
        return getProductByName(ingredientDto.getName())
                .orElseGet(() -> {
                    NutritionalValue nutritionalValue = ingredientDto.getNutritionalValue();
                    Product product = new Product();
                    product.setName(ingredientDto.getName());
                    product.setNutritionalValue(NutritionalValue.builder()
                            .calories(nutritionalValue.getCalories())
                            .protein(nutritionalValue.getProtein())
                            .fat(nutritionalValue.getFat())
                            .carbohydrate(nutritionalValue.getCarbohydrate())
                            .build());
                    return addProduct(product);
                });
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }


}

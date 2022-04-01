package edu.lysak.recipes.service;

import edu.lysak.recipes.model.Product;
import edu.lysak.recipes.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("#getProductByName(String) should successfully get existing product")
    void getProductByName_shouldSuccessfullyGetProduct() {
        Product product = new Product("milk");
        when(productRepository.findByName(any())).thenReturn(Optional.of(product));

        Optional<Product> productFromDb = productService.getProductByName("milk");

        verify(productRepository).findByName("milk");
        assertTrue(productFromDb.isPresent());
        assertEquals("milk", productFromDb.get().getName());
    }

    @Nested
    @DisplayName("#saveAndGetProduct(String)")
    class SaveAndGetProductMethodTest {

        @Test
        @DisplayName("should successfully get existing product")
        void saveAndGetProduct_shouldGetExistingProduct() {
            Product product = new Product("milk");
            when(productRepository.findByName(any())).thenReturn(Optional.of(product));

            Product productFromDb = productService.saveAndGetProduct("milk");

            verify(productRepository).findByName("milk");
            verify(productRepository, never()).save(any());
            assertEquals("milk", productFromDb.getName());
        }

        @Test
        @DisplayName("should successfully add new product")
        void saveAndGetProduct_shouldAddNewProduct() {
            when(productRepository.findByName(any())).thenReturn(Optional.empty());
            when(productRepository.save(any())).thenAnswer(returnsArgAt(0));

            Product savedProduct = productService.saveAndGetProduct("milk");

            verify(productRepository).findByName("milk");
            verify(productRepository).save(savedProduct);
            assertEquals("milk", savedProduct.getName());
        }
    }

    @Test
    @DisplayName("#addProduct(Product) should successfully save new product")
    void addProduct_shouldSuccessfullySaveProduct() {
        Product product = new Product("milk");
        when(productRepository.save(any())).thenAnswer(returnsArgAt(0));

        Product savedProduct = productService.addProduct(product);

        verify(productRepository).save(product);
        assertEquals("milk", savedProduct.getName());
    }
}

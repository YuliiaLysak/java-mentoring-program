package edu.lysak.recipes.repository;

import edu.lysak.recipes.TestUtil;
import edu.lysak.recipes.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByName() {
        Product product = TestUtil.getMockedProduct();
        assertNull(product.getId());
        productRepository.save(product);

        Optional<Product> fromDb = productRepository.findByName(product.getName());

        assertTrue(fromDb.isPresent());
        assertEquals(product.getName(), fromDb.get().getName());
        assertNotNull(fromDb.get().getId());
    }
}

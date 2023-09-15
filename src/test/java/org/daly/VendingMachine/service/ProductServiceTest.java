package org.daly.VendingMachine.service;

import org.daly.VendingMachine.controller.ProductNotFoundException;
import org.daly.VendingMachine.model.Product;
import org.daly.VendingMachine.repository.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    @Nested
    class GetProducts {
        @Test
        public void shouldGetProductFromRepository() {
            // Given a repository of products

            // When requesting a list of products
            productService.getProducts();

            // Then the repository should be searched for the products
            verify(productRepository, times(1)).findAll();
        }

        @Test
        public void shouldReturnProductsFromTheRepository() {
            // Given a repository of products
            Product product = mock(Product.class);
            List<Product> productList = List.of(product);
            doReturn(productList).when(productRepository).findAll();

            // When requesting a list of products
            List<Product> result = productService.getProducts();

            // Then the products from the repository should be returned
            assertThat("The products from the repository are returned", result, equalTo(productList));
        }
    }

    @Nested
    class GetAvailableProducts {
        @Test
        public void shouldGetAvailableProductFromRepository() {
            // Given a repository of products

            // When requesting a list of available products
            productService.getAvailableProducts();

            // Then the repository should be searched for the available products
            verify(productRepository, times(1)).findAllByQuantityAvailableGreaterThan(0);
        }

        @Test
        public void shouldReturnProductsFromTheRepository() {
            // Given a repository of products
            Product product = mock(Product.class);
            List<Product> productList = List.of(product);
            doReturn(productList).when(productRepository).findAllByQuantityAvailableGreaterThan(0);

            // When requesting a list of available products
            List<Product> result = productService.getAvailableProducts();

            // Then the available products from the repository should be returned
            assertThat("The available products from the repository are returned", result, equalTo(productList));
        }
    }

    @Nested
    class GetProductById {
        @Test
        public void shouldGetAvailableProductFromRepository() {
            // Given a product in the repository
            int productId = 5;
            doReturn(Optional.of(mock(Product.class))).when(productRepository).findById(productId);

            // When requesting the specific product
            productService.getProductById(productId);

            // Then the repository should be searched for the product
            verify(productRepository, times(1)).findById(productId);
        }

        @Test
        public void shouldReturnFoundProductFromTheRepository() {
            // Given a product in the repository
            Product product = mock(Product.class);
            int productId = 5;
            doReturn(Optional.of(product)).when(productRepository).findById(productId);

            // When requesting the product
            Product result = productService.getProductById(productId);

            // Then the product from the repository is returned
            assertThat("The found product is returned", result, equalTo(product));
        }

        @Test
        public void shouldThrowProductNotFoundExceptionIfProductDoesNotExist() {
            // Given the repository does not contain the requested product
            int productId = 5;
            doReturn(Optional.empty()).when(productRepository).findById(productId);

            // When requesting the product
            // Then a product not found exception should be thrown
            assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
        }
    }

    @Nested
    class CheckProductAvailability {
        @Test
        public void shouldReturnTrueIfProductIsAvailable() {
            // Given a product that has stock
            Product product = mock(Product.class);
            doReturn(1).when(product).getQuantityAvailable();
            int productId = 5;
            doReturn(Optional.of(product)).when(productRepository).findById(productId);

            // When checking if the product is available
            boolean result = productService.checkProductAvailability(productId);

            // Then true should be returned
            assertThat("Product is verified as being in stock", result, is(true));
        }

        @Test
        public void shouldReturnProductsFromTheRepository() {
            // Given a product that has stock
            Product product = mock(Product.class);
            doReturn(0).when(product).getQuantityAvailable();
            int productId = 5;
            doReturn(Optional.of(product)).when(productRepository).findById(productId);

            // When checking if the product is available
            boolean result = productService.checkProductAvailability(productId);

            // Then true should be returned
            assertThat("Product is verified as not being in stock", result, is(false));
        }

        @Test
        public void shouldThrowProductNotFoundExceptionIfProductDoesNotExist() {
            // Given the repository does not contain the requested product
            int productId = 5;
            doReturn(Optional.empty()).when(productRepository).findById(productId);

            // When requesting the product
            // Then a product not found exception should be thrown
            assertThrows(ProductNotFoundException.class, () -> productService.checkProductAvailability(productId));
        }
    }

}
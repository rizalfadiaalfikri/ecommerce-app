package id.orbion.ecommerce_app.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.error.InventoryException;
import id.orbion.ecommerce_app.entity.Product;
import id.orbion.ecommerce_app.repository.ProductRepository;
import id.orbion.ecommerce_app.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public boolean checkAndLockInventory(Map<Long, Integer> productQuantities) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Product product = productRepository.findByIdWithPessimistickLock(entry.getKey())
                    .orElseThrow(() -> new InventoryException("Product not found"));
            if (product.getStockQuantity() < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    @Override
    @Transactional
    public void decreaseQuantity(Map<Long, Integer> productQuantities) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Product product = productRepository.findByIdWithPessimistickLock(entry.getKey())
                    .orElseThrow(() -> new InventoryException("Product not found"));
            if (product.getStockQuantity() < entry.getValue()) {
                throw new InventoryException("Not enough inventory");
            }

            Integer newStockQuantity = product.getStockQuantity() - entry.getValue();
            product.setStockQuantity(newStockQuantity);
            productRepository.save(product);
        }
    }

    @Override
    @Transactional
    public void increaseQuantity(Map<Long, Integer> productQuantities) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Product product = productRepository.findByIdWithPessimistickLock(entry.getKey())
                    .orElseThrow(() -> new InventoryException("Product not found"));

            Integer newStockQuantity = product.getStockQuantity() + entry.getValue();
            product.setStockQuantity(newStockQuantity);
            productRepository.save(product);
        }
    }

}

package com.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.exception.BusinessException;
import com.shop.model.Product;
import com.shop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 查询所有商品
     *
     * @return 商品列表
     */
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    /**
     * 创建新商品
     *
     * @param product 商品对象
     * @return 创建后的商品
     */
    @Transactional
    public Product create(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }

    /**
     * 更新商品信息
     *
     * @param id      商品 ID
     * @param product 更新的商品信息
     * @return 更新后的商品
     */
    @Transactional
    public Product update(Long id, Product product) {
        // 检查商品是否存在
        if (!productRepository.existsById(id)) {
            throw new BusinessException("商品不存在");
        }
        
        validateProduct(product);
        product.setId(id);
        return productRepository.save(product);
    }

    /**
     * 删除商品
     *
     * @param id 商品 ID
     */
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException("商品不存在");
        }
        productRepository.deleteById(id);
    }

    /**
     * 搜索商品
     *
     * @param query 搜索关键词
     * @return 匹配的商品列表
     */
    public List<Product> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return listAll();
        }
        return productRepository.findByNameContainingIgnoreCase(query.trim());
    }

    /**
     * 根据 ID 查找商品
     *
     * @param id 商品 ID
     * @return 商品对象
     */
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
    }

    /**
     * 校验商品信息
     *
     * @param product 商品对象
     */
    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BusinessException("商品名称不能为空");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new BusinessException("商品价格不能为空或负数");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new BusinessException("商品库存不能为空或负数");
        }
    }
}



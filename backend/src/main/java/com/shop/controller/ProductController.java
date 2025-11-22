package com.shop.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.dto.ResponseDTO;
import com.shop.model.Product;
import com.shop.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 查询全部产品
     *
     * @return 产品列表
     */
    @GetMapping
    public ResponseDTO<List<Product>> list() {
        return ResponseDTO.ok(productService.listAll());
    }

    /**
     * 搜索产品，可根据关键字查询
     *
     * @param query 搜索关键词
     * @return 匹配的产品列表
     */
    @GetMapping("/search")
    public ResponseDTO<List<Product>> search(
            @RequestParam(value = "query", required = false) String query) {
        return ResponseDTO.ok(productService.search(query));
    }

    /**
     * 创建新产品
     *
     * @param product 产品信息
     * @return 创建后的产品
     */
    @PostMapping
    public ResponseDTO<Product> create(@RequestBody Product product) {
        return ResponseDTO.ok(productService.create(product));
    }

    /**
     * 更新已有产品
     *
     * @param id      产品 ID
     * @param product 更新的产品信息
     * @return 更新后的产品
     */
    @PutMapping("/{id}")
    public ResponseDTO<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseDTO.ok(productService.update(id, product));
    }

    /**
     * 删除产品
     *
     * @param id 产品 ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public ResponseDTO<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseDTO.ok(null);
    }
}




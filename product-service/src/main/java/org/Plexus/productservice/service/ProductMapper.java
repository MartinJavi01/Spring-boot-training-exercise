package org.Plexus.productservice.service;

import org.Plexus.productservice.dto.ProductDTO;
import org.Plexus.productservice.model.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    public List<ProductDTO> toDTOList(List<Product> products) {
        List<ProductDTO> productsDTO = new ArrayList<>();
        for(Product p: products) {
            ProductDTO pDto = new ProductDTO();
            BeanUtils.copyProperties(p, pDto);
            productsDTO.add(pDto);
        }
        return productsDTO;
    }

    public ProductDTO toDTO(Product product) {
        ProductDTO pDto = new ProductDTO();
        BeanUtils.copyProperties(product, pDto);
        return pDto;
    }
}

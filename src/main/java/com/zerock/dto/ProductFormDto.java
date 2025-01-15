package com.zerock.dto;

import com.zerock.Entity.ProductionService;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductFormDto {

    private Long id;

    private String title;

    private String category;

    private String subCategory;

    private String description;

    private Integer basePrice;

    private Integer maxWorkingDays;

    private Integer freeRevisions;

    private String communicationMethod;

    private List<ProductImgDto> productImgDtoList = new ArrayList<>();

    private List<Long>  productIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public ProductionService createProductionService() {
        return modelMapper.map(this, ProductionService.class);
    }

    public static ProductionService of(ProductionService productionService) {
        return modelMapper.map(productionService, ProductionService.class);
    }
}

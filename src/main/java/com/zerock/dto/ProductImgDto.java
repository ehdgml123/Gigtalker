package com.zerock.dto;

import com.zerock.Entity.ProductionServiceImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ProductImgDto{

private Long id;

private String imgName;

private String oriImgName;

private String imgUrl;

private static ModelMapper modelMapper = new ModelMapper();

public static ProductImgDto of(ProductionServiceImg productionServiceImg) {
    return modelMapper.map(productionServiceImg, ProductImgDto.class);
}

}

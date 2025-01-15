package com.zerock.service;

import com.zerock.Entity.ProductionServiceImg;
import com.zerock.Repository.ProductImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ProductImgRepository productImgRepository;
    private final FileService fileService;

    public void saveProductImg(ProductionServiceImg productionServiceImg, MultipartFile file) throws Exception {

        String oriImgName = file.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, file.getBytes());
            imgUrl = "/images/item/" + imgName; // 올바른 경로 설정
        }

        productionServiceImg.updateProductionServiceImg(oriImgName, imgName, imgUrl);
        productImgRepository.save(productionServiceImg);
    }
}

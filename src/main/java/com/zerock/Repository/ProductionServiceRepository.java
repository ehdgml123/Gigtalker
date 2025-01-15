package com.zerock.Repository;


import com.zerock.Entity.ProductionService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductionServiceRepository extends JpaRepository<ProductionService, Long> {

    List<ProductionService> findByCategory(String category);

    @Query("SELECT p FROM ProductionService p LEFT JOIN FETCH p.productionServiceImgs WHERE p.category = :category")
    List<ProductionService> findWithImagesByCategory(@Param("category") String category);
}

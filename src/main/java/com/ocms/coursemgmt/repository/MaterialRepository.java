package com.ocms.coursemgmt.repository;

import com.ocms.coursemgmt.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material,Long> {
    List<Material> findByCourseId(Long courseId);

}

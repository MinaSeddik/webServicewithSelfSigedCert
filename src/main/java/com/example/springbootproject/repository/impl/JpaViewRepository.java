package com.example.springbootproject.repository.impl;


import com.example.springbootproject.domain.SummaryView;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
@Profile("spring-data-jpa")
public interface JpaViewRepository extends PagingAndSortingRepository<SummaryView, String> {
    List<SummaryView> findByEmpRecruitedDateBetweenAndHhId(Date startDate, Date endDate, String hhId);
}

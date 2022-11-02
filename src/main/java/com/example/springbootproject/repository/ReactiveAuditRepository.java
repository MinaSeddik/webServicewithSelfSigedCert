package com.example.springbootproject.repository;

import com.example.springbootproject.model.AuditRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveAuditRepository extends ReactiveCrudRepository<AuditRecord, Integer> {
}

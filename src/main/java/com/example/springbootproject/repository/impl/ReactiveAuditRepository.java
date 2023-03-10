package com.example.springbootproject.repository.impl;

import com.example.springbootproject.domain.AuditRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveAuditRepository extends ReactiveCrudRepository<AuditRecord, Integer> {
}

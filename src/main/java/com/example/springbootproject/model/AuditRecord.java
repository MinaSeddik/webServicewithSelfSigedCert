package com.example.springbootproject.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@Builder
@Table("audit")
public class AuditRecord {

    @Id
    private int id;

    private String event;

}

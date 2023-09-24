package com.example.springbootproject.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Immutable
@Table(name = "hunters_summary")
@Subselect("select uuid() as id, hs.* from hunters_summary hs")
@Getter
@Setter
public class SummaryView implements Serializable {

    @Id
    private String id;
    private Long empId;
    private String hhId;

}
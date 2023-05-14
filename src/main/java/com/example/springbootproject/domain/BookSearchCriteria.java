package com.example.springbootproject.domain;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;



//http://localhost:8080/filter2?searchTerm=koko-lolo&isShipped=true&price=15



@Setter
@Getter
public class BookSearchCriteria {

    // used to search by code Id, author, isbn, firstname, lastname
//    @ColumnNames("code, isbn, author, firstname, lastname")
    @ColumnNames({"code", "isbn", "author", "firstname", "lastname"})
    private String searchTerm;

    // used to filter by shipped - represents check box
    @ColumnName("is_shipped")
    private Boolean isShipped;

    // used to filter by shipped - represents check box
    @ColumnName("price")
    private Integer price;

    // search by book status
    @ColumnName("status")
    private BookStatus bookStatus;

    // search by title
    @ColumnName("title")
    private String title;

    // filter by new or old - represents multiple selection combo-box
    @ColumnName("statuses")
    private List<String> statuses;  // in-closure

    // filter by printing date
    @ColumnName("date_printed_at")
    private LocalDate printingDate;

    @ColumnName("datetime_printed_at")
    private LocalDateTime printingDateTime;

    // filter by date range
    // used to filter by shipped - represents check box
    @ColumnName("issued_at")
    private DateRange issuedDateRange;


}

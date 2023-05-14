package com.example.springbootproject.controller;

import com.example.springbootproject.domain.Book;
import com.example.springbootproject.domain.BookSearchCriteria;
import com.example.springbootproject.domain.DateRange;
import com.example.springbootproject.exception.MyException;
import com.example.springbootproject.mvc.editor.CustomIntegerEditor;
import com.example.springbootproject.mvc.editor.CustomLocalDateEditor;
import com.example.springbootproject.mvc.editor.CustomLocalDateTimeEditor;
import com.example.springbootproject.service.BookService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@Slf4j
public class FilterController {

    @Autowired
    private BookService bookService;


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomIntegerEditor());
        binder.registerCustomEditor(LocalDate.class, new CustomLocalDateEditor());
        binder.registerCustomEditor(LocalDateTime.class, new CustomLocalDateTimeEditor());
    }



//    http://localhost:8080/filter2

//    http://localhost:8080/filter2?searchTerm=koko-lolo&isShipped=true&price=15&bookStatus=NEW

//    http://localhost:8080/filter2?searchTerm=koko-lolo&isShipped=true&price=15&bookStatus=NEW&title=polpol&statuses=X,Y,Z&printingDate=05-13-2023&printingDateTime=2023-05-14%2010:50&startDate=01-01-2001&endDate=05-01-2023


//    http://localhost:8080/filter2?page=0&size=3&sort=id&direction=DESC
//    http://localhost:8080/filter2?page=0&size=3&sort=firstname&sort=lastname,asc
//    http://localhost:8080/filter2?page=0&size=3&sort=firstname&sort=lastname,asc&sort=title,desc

    @GetMapping("/filter2")
    public Page<Book> filter2(BookSearchCriteria bookSearchCriteria,
                              DateRange dateRange,
                              Pageable pageable) {

        log.info("Inside filter2 Controller ...");
        bookSearchCriteria.setIssuedDateRange(dateRange);

        // log PageRequest
//        log.info("Pageable::Page Number: {}", pageable.getPageNumber());
//        log.info("Pageable::Page Size: {}", pageable.getPageSize());
//        log.info("Pageable::Page Offset: {}", pageable.getOffset());
//        log.info("Pageable::Page Request - Sort: {}", pageable.getSort());
//        log.info("Pageable::Page Request - Sort list: {}", pageable.getSort().toList());


//        BookSearchCriteria bookSearchCriteria = new BookSearchCriteria();
//        bookSearchCriteria.setSearchTerm("Mina");
//        bookSearchCriteria.setIsShipped(true);
//        bookSearchCriteria.setPrice(568);
//        bookSearchCriteria.setBookStatus(BookStatus.NEW);
//        bookSearchCriteria.setTitle("Book title ABC");
//        bookSearchCriteria.setStatuses(Arrays.asList("X", "Y", "Z"));
//        bookSearchCriteria.setPrintingDate(LocalDate.now());
//        bookSearchCriteria.setPrintingDateTime(LocalDateTime.now());
//        DateRange dateRange = new DateRange(LocalDate.now().minusYears(10), LocalDate.now().plusDays(1));
//        bookSearchCriteria.setIssuedDateRange(dateRange);

//        log.info("bookSearchCriteria::SearchTerm: {}", bookSearchCriteria.getSearchTerm());
//        log.info("bookSearchCriteria::getIsShipped: {}", bookSearchCriteria.getIsShipped());
//        log.info("bookSearchCriteria::getPrice: {}", bookSearchCriteria.getPrice());
//        log.info("bookSearchCriteria::getBookStatus: {}", bookSearchCriteria.getBookStatus());
//        log.info("bookSearchCriteria::getTitle: {}", bookSearchCriteria.getTitle());
//        log.info("bookSearchCriteria::getStatuses: {}", bookSearchCriteria.getStatuses());
//        log.info("bookSearchCriteria::getPrintingDate: {}", bookSearchCriteria.getPrintingDate());
//        log.info("bookSearchCriteria::getPrintingDateTime: {}", bookSearchCriteria.getPrintingDateTime());
//        log.info("bookSearchCriteria::getStartDate:: {}", bookSearchCriteria.getIssuedDateRange().getStartDate());
//        log.info("bookSearchCriteria::getEndDate:: {}", bookSearchCriteria.getIssuedDateRange().getEndDate());


        return bookService.findPaginatedFilter(bookSearchCriteria, pageable);
//        return "Okay";
    }


}

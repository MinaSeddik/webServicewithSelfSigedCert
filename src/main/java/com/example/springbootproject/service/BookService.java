package com.example.springbootproject.service;

import com.example.springbootproject.domain.Book;
import com.example.springbootproject.domain.BookSearchCriteria;
import com.example.springbootproject.repository.impl.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Page<Book> findPaginated(int page, int size) {

        return null;
    }

    public Page<Book> findPaginated(Pageable pageable) {


        return null;
    }

    public Page<Book> findPaginated(PageRequest pageRequest) {

        bookRepository.getBooks(pageRequest);
        return null;
    }

    public Page<Book> findPaginatedFilter(String searchTerm, String bookTitle, PageRequest pageRequest) {

        return null;
    }

    public Page<Book> findPaginatedFilter(BookSearchCriteria searchCriteria, Pageable pageable) {
        Page<Book> page = bookRepository.getBooks(searchCriteria, pageable);

        return page;
    }
}

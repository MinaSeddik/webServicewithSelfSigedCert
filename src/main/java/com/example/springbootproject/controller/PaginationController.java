package com.example.springbootproject.controller;

import com.example.springbootproject.domain.Book;
import com.example.springbootproject.exception.ResourceNotFoundException;
import com.example.springbootproject.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class PaginationController {

    @Autowired
    private BookService bookService;


    @GetMapping("/books")
    public List<Book> getBooks(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") List<String> sort,
            @RequestParam("direction") Optional<Boolean> direction) {

        PageRequest pageRequest = createPageRequest(page, size, sort, direction.orElse(false));
        Page<Book> resultPage = bookService.findPaginated(pageRequest);




        if (page > resultPage.getTotalPages()) {
            throw new ResourceNotFoundException();
        }

//        eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Foo>(
//                Foo.class, uriBuilder, response, page, resultPage.getTotalPages(), size));

        return resultPage.getContent();
    }


    /*
        we could have used a Pageable object, which maps the page, size,
        and sort parameters automatically.
     */
    @GetMapping("/books2")
    public Page<Book> getBooks2(Pageable pageable) {


        log.info("pageable.getPage() = {}", pageable.getPageNumber());
        log.info("pageable.getSize() = {}", pageable.getPageSize());
        log.info("pageable.getSort() = {}", pageable.getSort());  // Array of Strings


        Page<Book> resultPage = bookService.findPaginated(pageable);
//        Page<Book> resultPage = bookService.findPaginated(page, size);
//        Page<Book> resultPage = bookService.findPaginated(pageable.getPage(), pageable.getSize());


        return resultPage;
    }



    private PageRequest createPageRequest(int page, int size, List<String> sortBy, boolean direction) {
        Sort.Direction sorDirection = direction ? Sort.Direction.DESC : Sort.Direction.ASC;


//        PageRequest pageable1 =  PageRequest.of(1, 10, Sort.Direction.ASC, "title", "description");
//        PageRequest pageable2 =  PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "name"));

        return PageRequest.of(page, size, sorDirection, sortBy.toArray(String[]::new));
    }


    /*
    private void addLinkHeaderOnPagedResourceRetrieval(
            UriComponentsBuilder uriBuilder, HttpServletResponse response,
            Class clazz, int page, int totalPages, int size ){

        String resourceName = clazz.getSimpleName().toString().toLowerCase();
        uriBuilder.path( "/api/" + resourceName );

        StringJoiner linkHeader = new StringJoiner(", ");
        if (hasNextPage(page, totalPages)){
            String uriForNextPage = constructNextPageUri(uriBuilder, page, size);
            linkHeader.add(createLinkHeader(uriForNextPage, "next"));
        }

        Link link = linkTo(methodOn(CustomerController.class)
                .getOrdersForCustomer(customerId)).withSelfRel();

        response.addHeader("Link", linkHeader.toString());
    }

    private boolean hasNextPage(int page, int totalPages) {

        return page < totalPages;
    }

    String constructNextPageUri(UriComponentsBuilder uriBuilder, int page, int size) {
        return uriBuilder.replaceQueryParam(PAGE, page + 1)
                .replaceQueryParam("size", size)
                .build()
                .encode()
                .toUriString();
    }
*/
}

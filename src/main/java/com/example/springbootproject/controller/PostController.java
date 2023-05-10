package com.example.springbootproject.controller;

import com.example.springbootproject.domain.Post;
import com.example.springbootproject.dto.PostDto;
import com.example.springbootproject.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

@RestController
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;


    private ModelMapper modelMapper = new ModelMapper();

    public PostController() {

        TypeMap<Post, PostDto> propertyMapper = modelMapper.createTypeMap(Post.class, PostDto.class);

        // setup
        // have different property name
        propertyMapper.addMapping(Post::getTimestamp, PostDto::setCreationDate);

        // setup
        // add deep mapping to flatten source's creator object into a single field in destination
        propertyMapper.addMappings(
                mapper -> mapper.map(src -> src.getCreator().getUsername(), PostDto::setUsername)
        );

        // setup
        // skip property
        propertyMapper.addMappings(mapper -> mapper.skip(PostDto::setId));

        // setup
        // converter one type to another type
        Converter<Collection, Integer> collectionToSize = c -> c.getSource().size();
        propertyMapper.addMappings(
                mapper -> mapper.using(collectionToSize).map(Post::getComments, PostDto::setCommentCount)
        );


    }

    @GetMapping(value = "/post")
    public PostDto getPost() {

        Post post = postService.getSomePost();

        PostDto postDto = modelMapper.map(post, PostDto.class);

        return postDto;
    }

    //    When a client needs to replace an existing Resource entirely, they can use PUT.
    //    When they're doing a partial update, they can use HTTP PATCH.
    //    PUT is idempotent; PATCH can be idempotent but isn't required to be
    @PutMapping(value = "/post/{id}")
    public PostDto fullUpdate(@Valid @RequestBody PostDto postDto, @PathVariable("id") String id) {

        Post post = modelMapper.map(postDto, Post.class);


        post = postService.updatePost(id, post);

        PostDto outPostDto = modelMapper.map(post, PostDto.class);


        /*
            =====================================
            HTTP RESPONSE
            =====================================
        */
//        Put request usually responds with status code 200 when it is successful.


        return outPostDto;
    }

    //    When a client needs to replace an existing Resource entirely, they can use PUT.
    //    When they're doing a partial update, they can use HTTP PATCH.
    //    PUT is idempotent; PATCH can be idempotent but isn't required to be
    @PatchMapping(value = "/post/{id}")
    public PostDto partialUpdate(@RequestBody Map<String, Object> updatesMap,
                                 @PathVariable("id") String id) {
        // very important method for PATCH method when a specific field need to be updated

//        Partial Updates with JsonNullable
//        non-null value: a value like “abcd”, 76 and []
//        null value: well, a null value.
//        undefined value: an omitted value.

//        Handling Partial Requests With Null Values

        /*
            =====================================
            Solution(1)
            =====================================
        */

        ModelMapper mapper = new ModelMapper();

        // setup
        // IMPORTANT Case
        // So, in principle, we have a persisted Post domain, and we fetch it from its repository.
        //After that, we update the Post instance by merging another Post object into it:
        TypeMap<Post, Post> propertyMapper = mapper.createTypeMap(Post.class, Post.class);

        // a provider to fetch a Post instance from a repository
        Provider<Post> postProvider = p -> postService.findById(1L);
        propertyMapper.setProvider(postProvider);

        // setup
        // Conditional Mapping
        propertyMapper.addMappings(_mapper -> _mapper.when(Conditions.isNull()).skip(Post::getId, Post::setId));


        /*
                // we can create custom condition too
        TypeMap<Post, PostDto> _propertyMapper = mapper.createTypeMap(Post.class, PostDto.class);
        Condition<Long, Long> hasTimestamp = ctx -> ctx.getSource() != null && ctx.getSource() > 0;
        _propertyMapper.addMappings(
                _mapper -> _mapper.when(hasTimestamp).map(Post::getTimestamp, PostDto::setCreationTime)
        );
         */

        // more setups
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
//        modelMapper.getConfiguration().setSkipNullEnabled(true);
//        modelMapper.getConfiguration().setPreferNestedProperties(false);  // for Circular references


        Post post = postService.getSomePost();

        // when a state for update is given
        Post modifiedPost = postService.getSomePost();
        modifiedPost.setName("After-update");
        Post updatedPost = modelMapper.map(modifiedPost, Post.class);

        PostDto outDto = modelMapper.map(updatedPost, PostDto.class);

        /*
            =====================================
            Solution(2)
            =====================================
        */

        ModelMapper mapper2 = new ModelMapper();
        PostDto postDto = mapper.map(updatesMap, PostDto.class);
//        then continue with Solution (1), based on your case


        /*
            =====================================
            HTTP RESPONSE
            =====================================
        */

//        PATCH request can respond with status code 200 when it is successful with Post payload

//        OR

//        HTTP/1.1 204 No Content
//        Content-Location: /file.txt
//        ETag: "e0023aa4f"
//
//        The 204 response code is used because the response does not carry a
//        message body (which a response with the 200 code would have).  Note
//        that other success codes could be used as well.

        return outDto;  // here will return the resource with HTTP code = 200
    }

    //    POST means "create new" as in "Here is the input for creating a user, create it for me".
    @PostMapping(value = "/post")
    public ResponseEntity<PostDto> create(@Valid @RequestBody PostDto postDto) {

        ModelMapper mapper = new ModelMapper();
        Post post = modelMapper.map(postDto, Post.class);


        Post outPost = postService.createPost(post);

        PostDto outDto = modelMapper.map(outPost, PostDto.class);

        /*
            =====================================
            HTTP RESPONSE
            =====================================
        */

//        If a resource has been created on the origin server, the response SHOULD be 201 (Created)
//        and contain an entity which describes the status of the request and refers to the new resource,
//        and a Location header (see section 14.30).

//        Reference: https://stackoverflow.com/questions/1226810/is-http-post-request-allowed-to-send-back-a-response-body
//        It is perfectly acceptable to specify a response body and use the Location header

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(outPost.getId())
                .toUri();

//        return ResponseEntity.created(location).build();

//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(outDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toString())
                .body(outDto);

    }
}

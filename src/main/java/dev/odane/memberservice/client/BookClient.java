package dev.odane.memberservice.client;


import dev.odane.memberservice.model.Book;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "book-service",url = "${spring.application.config.book-url}")
public interface BookClient {

    @GetMapping("/{book-id}")
    Book getBookById (@PathVariable("book-id") UUID bookId);

    @PostMapping()
    Book saveBook(@RequestBody Book book);
}

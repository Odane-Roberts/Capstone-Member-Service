package dev.odane.memberservice.controller;


import dev.odane.memberservice.dtos.MemberDTO;
import dev.odane.memberservice.model.Book;
import dev.odane.memberservice.model.BorrowedBook;
import dev.odane.memberservice.model.Member;
import dev.odane.memberservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController {

    private final MemberService service;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('client_admin')")
    public Page<MemberDTO> getMembers(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int pageSize) {

        log.info("Get all members request received");
        Page<MemberDTO> members = service.findAllMembers(page, pageSize);
        log.info("Members retrieved");
        return members;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable UUID id) {
        log.info("Get member by ID request received");
        Member member = service.findById(id);
        log.info("Member retrieved");
        return member;
    }


    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping
    public Member updateMember(@RequestBody Member member) {
        log.info("Update member request received");
        Member updatedMember = service.updateMember(member);
        log.info("Member updated");
        return updatedMember;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/borrowed")
    public List<BorrowedBook> getBorrowedBooks(@PathVariable UUID id) {
        log.info("Get borrowed books request received");
        List<BorrowedBook> borrowedBooks = service.getBorrowBooks(id);
        log.info("Borrowed books retrieved");
        return borrowedBooks;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/return")
    public String returnBooks(@RequestBody Book books) {
        log.info("Return books request received");
        String returnMessage = service.returnBooks(books);
        log.info("Books returned");
        return returnMessage;
    }


}

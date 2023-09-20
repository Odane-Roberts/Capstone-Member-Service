package dev.odane.memberservice.service;


import dev.odane.memberservice.client.BookClient;
import dev.odane.memberservice.dtos.MemberDTO;
import dev.odane.memberservice.exception.BorrowedBookNotFoundException;
import dev.odane.memberservice.exception.MemberNotFoundException;
import dev.odane.memberservice.mapper.MemberMapper;
import dev.odane.memberservice.model.Book;
import dev.odane.memberservice.model.BorrowedBook;
import dev.odane.memberservice.model.Member;
import dev.odane.memberservice.model.Status;
import dev.odane.memberservice.repository.BorrowedBookRepository;
import dev.odane.memberservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repository;
    private final BorrowedBookRepository borrowedBookRepository;
    private final BookClient bookClient;
    private final MemberMapper memberMapper;

    @Override
    public Page<MemberDTO> findAllMembers(int page, int pageSize) {
        List<MemberDTO> memberDTOS = repository.findAll().stream()
                .map(memberMapper::memberToMemberDTO).toList();

        PageRequest pageRequest = PageRequest.of(page, pageSize);

        return new PageImpl<>(memberDTOS, pageRequest, memberDTOS.size());
    }

    @Override
    public Member findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->new MemberNotFoundException("Member not found "+id));
    }


    @Override
    public void removeMember(Member member) {
        repository.delete(member);
    }

    @Override
    public Member updateMember(Member member) {
        return repository.save(member);
    }

    @Override
    public List<BorrowedBook> getBorrowBooks(UUID id) {

        // TODO: 02/09/2023 refactor to use bag service
        return borrowedBookRepository.findBorrowedBookByMember(repository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found "+id)));
    }

    @Override
    public String returnBooks(Book book) {

        BorrowedBook borrowedBook = borrowedBookRepository.findBorrowedBookByBook(book)
                        .orElseThrow(() -> new BorrowedBookNotFoundException("Borrowed book not found "));
        borrowedBook.setDateReturned(LocalDateTime.now());
        borrowedBookRepository.save(borrowedBook);


        // TODO: 02/09/2023 use book service
        Book book1 = bookClient.getBookById(book.getId());
                book1.setStatus(Status.AVAILABLE);
        bookClient.saveBook(book1);

        return "Thanks for returning the book"; // return a status
    }


}

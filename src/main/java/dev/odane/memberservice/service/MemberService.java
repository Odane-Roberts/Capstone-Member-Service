package dev.odane.memberservice.service;


import dev.odane.memberservice.dtos.MemberDTO;
import dev.odane.memberservice.model.Book;
import dev.odane.memberservice.model.BorrowedBook;
import dev.odane.memberservice.model.Member;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.UUID;

public interface MemberService {
    Page<MemberDTO> findAllMembers(int page,int pageSize);
    Member findById(UUID id);
    void removeMember(Member member);
    Member updateMember(Member member);
    List<BorrowedBook> getBorrowBooks(UUID id);

    String returnBooks(Book books);

}

package dev.odane.memberservice.repository;


import dev.odane.memberservice.model.Book;
import dev.odane.memberservice.model.BorrowedBook;
import dev.odane.memberservice.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, UUID> {
    List<BorrowedBook> findBorrowedBookByMember(Member member);

    @Query("""
            SELECT b.title, COUNT(bt.id) AS borrow_count
            FROM Book b
            JOIN BorrowedBook bt ON b.id = bt.book.id
            GROUP BY b.title
            ORDER BY borrow_count DESC
            """)
    List<Object[]> findMostBorrowedBook();

    @Query(nativeQuery = true, value =
            "SELECT m.name, m.email, " +
                    "SUM(CASE WHEN bb.dateReturned > bb.dueDate " +
                    "THEN EXTRACT(DAY FROM bb.dateReturned - bb.dueDate) ELSE 0 END) AS days_outstanding " +
                    "FROM borrowed_book bb " +
                    "JOIN borrower m ON bb.id = m.borrowerid " +
                    "GROUP BY m.name, m.email")
    List<Object[]> findMembersWhoOwe();

    Optional<BorrowedBook> findBorrowedBookByBook(Book book);
}

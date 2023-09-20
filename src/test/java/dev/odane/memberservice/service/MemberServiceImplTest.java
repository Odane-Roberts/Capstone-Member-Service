package dev.odane.memberservice.service;

import dev.odane.memberservice.client.BookClient;
import dev.odane.memberservice.dtos.MemberDTO;
import dev.odane.memberservice.exception.BorrowedBookNotFoundException;
import dev.odane.memberservice.exception.MemberNotFoundException;
import dev.odane.memberservice.mapper.MemberMapper;
import dev.odane.memberservice.model.*;
import dev.odane.memberservice.repository.BorrowedBookRepository;
import dev.odane.memberservice.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @Mock
    private BookClient bookClient;

    @Mock
    private MemberMapper memberMapper;

    private MemberServiceImpl memberService;


    Member member1 = Member.builder()
            .name("John Doe")
            .email("john@example.com")
            .password("password1")
            .gender(Gender.MALE)
            .dob(LocalDate.of(1990, 5, 15))
            .phone("123-456-7890")
            .status(MemberStatus.ACTIVE)
            .role(Role.MEMBER)
            .build();

    // Create the second member
    Member member2 = Member.builder()
            .name("Jane Smith")
            .email("jane@example.com")
            .password("password2")
            .gender(Gender.FEMALE)
            .dob(LocalDate.of(1985, 8, 22))
            .phone("987-654-3210")
            .status(MemberStatus.ACTIVE)
            .role(Role.MEMBER)
            .build();

    // Create the third member
    Member member3 = Member.builder()
            .name("Alice Johnson")
            .email("alice@example.com")
            .password("password3")
            .gender(Gender.FEMALE)
            .dob(LocalDate.of(1995, 3, 10))
            .phone("555-123-4567")
            .status(MemberStatus.INACTIVE)
            .role(Role.ADMIN)
            .build();

    Book book1 = Book.builder()
            .title("The Great Gatsby")
            .author("F. Scott Fitzgerald")
            .isbn("978-0-7432-7356-5")
            .publicationDate(LocalDateTime.of(1925, 4, 10, 0, 0))
            .category(Category.NONFICTION)
            .quantity(5)
            .status(Status.AVAILABLE)
            .build();

    // Create the second Book
    Book book2 = Book.builder()
            .title("To Kill a Mockingbird")
            .author("Harper Lee")
            .isbn("978-0-06-112008-4")
            .publicationDate(LocalDateTime.of(1960, 7, 11, 0, 0))
            .category(Category.FICTION)
            .quantity(3)
            .status(Status.AVAILABLE)
            .build();

    // Create the third Book
    Book book3 = Book.builder()
            .title("1984")
            .author("George Orwell")
            .isbn("978-0-452-28423-4")
            .publicationDate(LocalDateTime.of(1949, 6, 8, 0, 0))
            .category(Category.NONFICTION)
            .quantity(7)
            .status(Status.AVAILABLE)
            .build();

    BorrowedBook borrowedBook1 = BorrowedBook.builder()
            .dateBorrowed(LocalDateTime.now().minusDays(7))
            .dueDate(LocalDateTime.now().plusDays(14))
            .dateReturned(null) // Not yet returned
            .member(member1)
            .book(book1)
            .build();

    // Create the second BorrowedBook
    BorrowedBook borrowedBook2 = BorrowedBook.builder()
            .dateBorrowed(LocalDateTime.now().minusDays(10))
            .dueDate(LocalDateTime.now().plusDays(7))
            .dateReturned(LocalDateTime.now().minusDays(3))
            .member(member2)
            .book(book2)
            .build();

    // Create the third BorrowedBook
    BorrowedBook borrowedBook3 = BorrowedBook.builder()
            .dateBorrowed(LocalDateTime.now().minusDays(5))
            .dueDate(LocalDateTime.now().plusDays(21))
            .dateReturned(LocalDateTime.now().minusDays(1))
            .member(member3)
            .book(book3)
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        memberService = new MemberServiceImpl(memberRepository, borrowedBookRepository, bookClient, memberMapper);
    }

    @Test
    void testFindAllMembers() {
        // Arrange
        int page = 0;
        int pageSize = 10;
        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);

        when(memberRepository.findAll()).thenReturn(members);
        when(memberMapper.memberToMemberDTO(any())).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            return MemberDTO.builder()
                    .id(member1.getId())
                    .name(member1.getName())
                    .email(member1.getEmail())
                    .phone(member1.getPhone())
                    .build();
        });

        // Act
        Page<MemberDTO> result = memberService.findAllMembers(page, pageSize);

        // Assert
        assertNotNull(result);
        assertEquals(members.size(), result.getTotalElements());
        assertEquals(pageSize, result.getSize());
        assertEquals(members.size(), result.getContent().size());
    }

    @Test
    void testFindById_ExistingMember() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Member existingMember = member1;
        existingMember.setId(memberId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

        // Act
        Member result = memberService.findById(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(existingMember, result);
    }

    @Test
    void testFindById_NonExistentMember() {
        // Arrange
        UUID memberId = UUID.randomUUID();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(MemberNotFoundException.class, () -> memberService.findById(memberId));
    }

    @Test
    void testRemoveMember() {
        // Arrange
        Member memberToDelete = member1;

        // Act
        memberService.removeMember(memberToDelete);

        // Assert
        Mockito.verify(memberRepository, times(1)).delete(memberToDelete);
    }

    @Test
    void testUpdateMember() {
        // Arrange
        Member memberToUpdate = member1;

        when(memberRepository.save(any())).thenReturn(memberToUpdate);

        // Act
        Member result = memberService.updateMember(memberToUpdate);

        // Assert
        assertNotNull(result);
        assertEquals(memberToUpdate, result);
    }
    @Test
    void testGetBorrowBooks() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Member member = member1;
        member.setId(memberId);
        List<BorrowedBook> borrowedBooks = new ArrayList<>();
        borrowedBooks.add(borrowedBook1);
        borrowedBooks.add(borrowedBook2);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(borrowedBookRepository.findBorrowedBookByMember(member)).thenReturn(borrowedBooks);

        // Act
        List<BorrowedBook> result = memberService.getBorrowBooks(memberId);

        // Assert
        assertNotNull(result);
        assertEquals(borrowedBooks.size(), result.size());
    }

    @Test
    void testGetBorrowBooks_MemberNotFound() {
        // Arrange
        UUID memberId = UUID.randomUUID();

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(MemberNotFoundException.class, () -> memberService.getBorrowBooks(memberId));
    }

    @Test
    void testReturnBooks() {
        // Arrange
        Book bookToReturn = book1;
        BorrowedBook borrowedBook = borrowedBook1;

        when(borrowedBookRepository.findBorrowedBookByBook(bookToReturn)).thenReturn(Optional.of(borrowedBook));
        when(bookClient.getBookById(bookToReturn.getId())).thenReturn(bookToReturn);

        // Act
        String result = memberService.returnBooks(bookToReturn);

        // Assert
        assertNotNull(result);
        assertEquals("Thanks for returning the book", result);
        assertEquals(LocalDateTime.now().getDayOfMonth(), borrowedBook.getDateReturned().getDayOfMonth());
        assertEquals(Status.AVAILABLE, bookToReturn.getStatus());
    }

    @Test
    void testReturnBooks_BorrowedBookNotFound() {
        // Arrange
        Book bookToReturn = book1;

        when(borrowedBookRepository.findBorrowedBookByBook(bookToReturn)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(BorrowedBookNotFoundException.class, () -> memberService.returnBooks(bookToReturn));
    }

}
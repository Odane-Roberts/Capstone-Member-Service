package dev.odane.memberservice.controller;

import dev.odane.memberservice.controller.MemberController;
import dev.odane.memberservice.dtos.MemberDTO;
import dev.odane.memberservice.mapper.MemberMapper;
import dev.odane.memberservice.model.*;
import dev.odane.memberservice.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

 class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberController memberController;

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

    List<MemberDTO> memberDTOList = new ArrayList<>();
    // Create a Pageable object for pagination
    Pageable pageable = Pageable.unpaged(); // Unpaged means no specific page/size for testing

    // Create a Page<MemberDTO> using PageImpl
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithMockUser(roles = "client_admin")
     void testGetMembers() {
        // Arrange
        int page = 0;
        int pageSize = 10;
        Page<MemberDTO> expectedPage =new PageImpl<>(memberDTOList, pageable, memberDTOList.size());

        when(memberService.findAllMembers(page, pageSize)).thenReturn(expectedPage);

        // Act
        Page<MemberDTO> result = memberController.getMembers(page, pageSize);

        // Assert
        assertEquals(expectedPage, result);
    }

    @Test
     void testGetMemberById() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Member expectedMember = new Member();
        expectedMember.setId(memberId);
        when(memberService.findById(memberId)).thenReturn(expectedMember);

        // Act
        Member result = memberController.getMemberById(memberId);

        // Assert
        assertEquals(expectedMember, result);
    }

    @Test
   void testUpdateMember() {
        // Arrange
        Member memberToUpdate = member1/* create a Member object with test data */;
        Member expectedUpdatedMember = member2 /* create a Member object with updated data */;

        when(memberService.updateMember(memberToUpdate)).thenReturn(expectedUpdatedMember);

        // Act
        Member result = memberController.updateMember(memberToUpdate);

        // Assert
        assertEquals(expectedUpdatedMember, result);
    }

    @Test
     void testGetBorrowedBooks() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        List<BorrowedBook> expectedBorrowedBooks = List.of(borrowedBook1,borrowedBook2,borrowedBook3);

        when(memberService.getBorrowBooks(memberId)).thenReturn(expectedBorrowedBooks);

        // Act
        List<BorrowedBook> result = memberController.getBorrowedBooks(memberId);

        // Assert
        assertEquals(expectedBorrowedBooks, result);
    }

    @Test
    public void testReturnBooks() {
        // Arrange
        Book booksToReturn = book1; /* create a Book object with test data */
        String expectedReturnMessage = "Books returned successfully";

        when(memberService.returnBooks(booksToReturn)).thenReturn(expectedReturnMessage);

        // Act
        String result = memberController.returnBooks(booksToReturn);

        // Assert
        assertEquals(expectedReturnMessage, result);
    }
}

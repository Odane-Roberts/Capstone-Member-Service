package dev.odane.memberservice.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dev.odane.memberservice.dtos.MemberDTO;
import dev.odane.memberservice.model.*;
import dev.odane.memberservice.repository.AdminRepository;
import dev.odane.memberservice.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberControllerTest2 {
    @Mock
    private MemberService memberService;

    @Mock
    private AdminRepository adminRepository;


    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    MemberDTO memberDTO1 = MemberDTO.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .email("john@example.com")
            .phone("123-456-7890")
            .build();

    // Create the second MemberDTO
    MemberDTO memberDTO2 = MemberDTO.builder()
            .id(UUID.randomUUID())
            .name("Jane Smith")
            .email("jane@example.com")
            .phone("987-654-3210")
            .build();

    // Create the third MemberDTO
    MemberDTO memberDTO3 = MemberDTO.builder()
            .id(UUID.randomUUID())
            .name("Alice Johnson")
            .email("alice@example.com")
            .phone("555-123-4567")
            .build();

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


    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer(); // can be implemented better


    @Test
   void testGetMembers() throws Exception {
        // Create test data for members
        List<MemberDTO> memberDTOList = new ArrayList<>();
        memberDTOList.add(memberDTO1);
        memberDTOList.add(memberDTO2);

        // Create a Page<MemberDTO> with test data
        Page<MemberDTO> expectedPage = new PageImpl<>(memberDTOList);

        // Mock the behavior of memberService.findAllMembers
        when(memberService.findAllMembers(anyInt(), anyInt())).thenReturn(expectedPage);

        // Perform the GET request
        mockMvc.perform(get("/api/v1/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(memberDTOList.size()));

        // Verify that memberService.findAllMembers was called with the correct arguments
        verify(memberService, times(1)).findAllMembers(0, 10);
    }

    @Test
    void testGetMemberById() throws Exception {
        // Create a test Member and its UUID
        UUID memberId = UUID.randomUUID();
        Member testMember = new Member();
        testMember.setId(memberId);
        testMember.setName("Odane");

        // Mock the behavior of memberService.findById
        when(memberService.findById(memberId)).thenReturn(testMember);

        // Perform the GET request
        mockMvc.perform(get("/api/v1/member/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testMember.getName()));

        // Verify that memberService.findById was called with the correct argument
        verify(memberService, times(1)).findById(memberId);
    }

    @Test
    void testUpdateMember() throws Exception {
        // Create a test Member
        Member testMember = member1;

        // Mock the behavior of memberService.updateMember
        when(memberService.updateMember(any(Member.class))).thenReturn(testMember);

        objectMapper.findAndRegisterModules();
        String memberJson = objectMapper.writeValueAsString(testMember);

        // Perform the PUT request
        mockMvc.perform(put("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value(testMember.getName()));

        // Verify that memberService.updateMember was called with the correct argument
        verify(memberService, times(1)).updateMember(any(Member.class));
    }

    @Test
     void testGetBorrowedBooks() throws Exception {
        // Create a test Member and its UUID
        UUID memberId = UUID.randomUUID();

        // Create test data for borrowed books
        List<BorrowedBook> borrowedBooks = new ArrayList<>();
        borrowedBooks.add(new BorrowedBook(/* Populate with test data */));
        borrowedBooks.add(new BorrowedBook(/* Populate with test data */));

        // Mock the behavior of memberService.getBorrowBooks
        when(memberService.getBorrowBooks(memberId)).thenReturn(borrowedBooks);

        // Perform the GET request
        mockMvc.perform(get("/api/v1/member/{id}/borrowed", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(borrowedBooks.size()));

        // Verify that memberService.getBorrowBooks was called with the correct argument
        verify(memberService, times(1)).getBorrowBooks(memberId);
    }

    @Test
    void testReturnBooks() throws Exception {
        // Create a test Book
        Book testBook = book1;

        // Mock the behavior of memberService.returnBooks
        when(memberService.returnBooks(any(Book.class))).thenReturn("Books returned successfully");

        String testBookJson = objectMapper.writeValueAsString(testBook);
        // Perform the PUT request
        mockMvc.perform(put("/api/v1/member/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testBookJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Books returned successfully"));

        // Verify that memberService.returnBooks was called with the correct argument
        verify(memberService, times(1)).returnBooks(any(Book.class));
    }
}

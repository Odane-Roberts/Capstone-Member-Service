package dev.odane.memberservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@RequiredArgsConstructor
@Builder
@Entity
@Table(name = "borrowed_book")
@AllArgsConstructor
@JsonIgnoreProperties("member") // Exclude the member property from JSON serialization
public class BorrowedBook implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "UUID", parameters =
            {@org.hibernate.annotations.Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
    private UUID id;
    @Column(name = "dateborrowed")
    private LocalDateTime dateBorrowed;
    @Column(name = "duedate")
    private LocalDateTime dueDate;
    @Column(name = "datereturned")
    private LocalDateTime dateReturned;

    @ManyToOne
    @JoinColumn(name = "borrowerid")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    // ... other fields ...

    // Don't re-serialize the parent quiz when serializing questions
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    @JsonBackReference("quiz-questions")
    private Quiz quiz;

    // ... existing code ...
} 
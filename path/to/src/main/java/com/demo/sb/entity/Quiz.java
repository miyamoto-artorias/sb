import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    // ... other fields ...

    // Cascade questions → JSON will serialize questions but not back into quiz
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    @JsonManagedReference("quiz-questions")
    private List<QuizQuestion> questions = new ArrayList<>();

    // Link back to chapter without serializing the chapter's quizzes again
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    @JsonBackReference("chapter-quizzes")
    private CourseChapter chapter;

    // ... existing code ...
} 
@SpringBootApplication
public class QueueApplication {
    public static void main(String[] args) {
        SpringApplication.run(QueueApplication.class, args);
    }
}
@Document("tokens")
public class Token {
    @Id
    private String id;
    private String patientName;
    private int tokenNumber;
    private String status; // "waiting", "served"

    // Getters and Setters
}
public interface TokenRepository extends MongoRepository<Token, String> {
    List<Token> findByStatus(String status);
    Optional<Token> findTopByOrderByTokenNumberDesc();
}
@Service
public class TokenService {
    @Autowired
    private TokenRepository repo;

    public Token createToken(String name) {
        int lastToken = repo.findTopByOrderByTokenNumberDesc().map(Token::getTokenNumber).orElse(0);
        Token token = new Token();
        token.setPatientName(name);
        token.setTokenNumber(lastToken + 1);
        token.setStatus("waiting");
        return repo.save(token);
    }

    public List<Token> getQueue() {
        return repo.findByStatus("waiting");
    }

    public void markAsServed(String id) {
        Token token = repo.findById(id).orElseThrow();
        token.setStatus("served");
        repo.save(token);
    }
}
@RestController
@RequestMapping("/api/tokens")
@CrossOrigin
public class TokenController {
    @Autowired
    private TokenService service;

    @PostMapping
    public Token generateToken(@RequestParam String name) {
        return service.createToken(name);
    }

    @GetMapping
    public List<Token> getQueue() {
        return service.getQueue();
    }

    @PutMapping("/{id}/serve")
    public void markServed(@PathVariable String id) {
        service.markAsServed(id);
    }
}
spring.data.mongodb.uri=mongodb://localhost:27017/smart_queue
server.port=8080


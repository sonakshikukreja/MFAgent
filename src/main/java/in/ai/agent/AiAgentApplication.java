package in.ai.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiAgentApplication {
    public static void main(String[] args) {
        // WARNING: Disables SSL certificate validation for development purposes.
        // This is insecure and should not be used in production.
        SSLCertificateValidation.disable();

        SpringApplication.run(AiAgentApplication.class, args);
    }
}
package app.mkiniz.poctime.shared;

import app.mkiniz.poctime.PoctimeApplication;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = PoctimeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @MockBean
    protected RabbitTemplate rabbitTemplate;

    @MockBean
    protected RabbitAdmin rabbitAdmin;
}

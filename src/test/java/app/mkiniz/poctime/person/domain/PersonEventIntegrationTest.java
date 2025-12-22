package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.shared.AddBaseBusinessTest;
import app.mkiniz.poctime.shared.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RecordApplicationEvents
@Transactional
class PersonEventIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    private AddBaseBusinessTest<Person, Person> baseTest;
    private CpfDocument cpfDocument;

    @BeforeEach
    void setUp() {
        this.baseTest = AddBaseBusinessTest.<Person, Person>of();
        this.cpfDocument = new CpfDocument("123.456.789-00");
    }

    @Test
    void addPersonMostSendEvent() {
        this.baseTest
                .given(() -> {
                    Person person = Person.builder()
                            .id(1L)
                            .name("Event Tester")
                            .document(this.cpfDocument)
                            .build();
                    person.created();
                    return person;
                })
                .when(personRepository::save)
                .then((person, response) -> {
                    long eventosDisparados = applicationEvents.stream(PersonAddedEvent.class)
                            .filter(event -> event.document().equals(cpfDocument))
                            .count();

                    assertThat(eventosDisparados).isEqualTo(1);

                    applicationEvents.stream(PersonAddedEvent.class)
                            .findFirst()
                            .ifPresent(event -> {
                                assertThat(event.name()).isEqualTo("Event Tester");
                                assertThat(event.id()).isNotNull();
                            });
                }).execute();
    }

    @Test
    void updatePersonMostSendEvent() {
        this.baseTest
                .given(() -> {
                    Person person = Person.builder()
                            .id(1L)
                            .name("Event Tester")
                            .document(this.cpfDocument)
                            .build();
                    person.updated();
                    return person;
                })
                .when(personRepository::save)
                .then((person, response) -> {
                    long eventosDisparados = applicationEvents.stream(PersonUpdatedEvent.class)
                            .filter(event -> event.document().equals(cpfDocument))
                            .count();

                    assertThat(eventosDisparados).isEqualTo(1);

                    applicationEvents.stream(PersonUpdatedEvent.class)
                            .findFirst()
                            .ifPresent(event -> {
                                assertThat(event.name()).isEqualTo("Event Tester");
                                assertThat(event.id()).isNotNull();
                            });
                }).execute();
    }

    @Test
    void deletePersonMostSendEvent() {
        this.baseTest
                .given(() -> {
                    Person person = Person.builder()
                            .id(1L)
                            .name("Event Tester")
                            .document(this.cpfDocument)
                            .build();
                    person.deleted();
                    return person;
                })
                .when(person -> {
                    personRepository.delete(person);
                    return person;
                })
                .then((person, response) -> {
                    long eventosDisparados = applicationEvents.stream(PersonDeletedEvent.class)
                            .filter(event -> event.document().equals(cpfDocument))
                            .count();

                    assertThat(eventosDisparados).isEqualTo(1);

                    applicationEvents.stream(PersonDeletedEvent.class)
                            .findFirst()
                            .ifPresent(event -> {
                                assertThat(event.name()).isEqualTo("Event Tester");
                                assertThat(event.id()).isNotNull();
                            });
                }).execute();
    }
}

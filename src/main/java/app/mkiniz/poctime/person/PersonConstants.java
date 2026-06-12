package app.mkiniz.poctime.person;

public class PersonConstants {
    public static final String NAME_NOT_NULL = "person.name.not.null";
    public static final String DUPLICATED = "person.duplicated";
    public static final String NAME_NOT_BLANK = "person.name.not.blank";
    public static final String DOCUMENT_NOT_NULL = "person.document.not.null";
    public static final String ID_NOT_FOUND = "person.id.not.found";
    public static final String DOCUMENT_INVALID = "person.document.invalid";
    public static final String CANNOT_REMOVE_PERSON_ORGANIZATION = "person.cannot.remove.because.organization";
    public static final String CANNOT_REMOVE_PERSON_CLIENT = "person.cannot.remove.because.client";

    // RABBITMQ CONSTANTS
    public static final String PERSON_INPUT_EXCHANGE = "exchange.in.person";
    public static final String PERSON_OUTPUT_EXCHANGE = "exchange.out.person";
}

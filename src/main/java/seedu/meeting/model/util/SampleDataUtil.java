package seedu.meeting.model.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.meeting.MainApp;
import seedu.meeting.model.MeetingBook;
import seedu.meeting.model.ReadOnlyMeetingBook;
import seedu.meeting.model.person.Email;
import seedu.meeting.model.person.Name;
import seedu.meeting.model.person.Person;
import seedu.meeting.model.person.Phone;
import seedu.meeting.model.shared.Address;
import seedu.meeting.model.tag.Tag;
import seedu.meeting.storage.XmlMeetingBookStorage;

/**
 * Contains utility methods for populating {@code MeetingBook} with sample data.
 */
public class SampleDataUtil {

    private static final String DEFAULT_DATA_PATH = "/data/Default.xml";
    private static final String TEMP_FILE_NAME = "temp";
    private static final String TEMP_FILE_SUFFIX = ".tmp";

    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"),
                getTagSet("friends")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                getTagSet("colleagues", "friends")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                getTagSet("neighbours")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                getTagSet("family")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"),
                getTagSet("classmates")),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"),
                getTagSet("colleagues"))
        };
    }

    // @@author NyxF4ll
    /**
     * Returns path to the default MeetingBook data.
     */
    private static Path getPathToDefaultMeetingBook() throws IOException {
        InputStream defaultDataStream = MainApp.class.getResourceAsStream(DEFAULT_DATA_PATH);

        if (defaultDataStream == null) {
            throw new IOException();
        }

        File tempFile = File.createTempFile(TEMP_FILE_NAME, TEMP_FILE_SUFFIX);
        tempFile.deleteOnExit();

        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = defaultDataStream.readAllBytes();
        outputStream.write(buffer);

        return tempFile.toPath();
    }

    /**
     * Returns the default MeetingBook.
     */
    public static ReadOnlyMeetingBook getSampleMeetingBook() {
        try {
            Path defaultMeetingBookPath = getPathToDefaultMeetingBook();
            XmlMeetingBookStorage defaultMeetingBook = new XmlMeetingBookStorage(defaultMeetingBookPath);
            return defaultMeetingBook.readMeetingBook().get();
        } catch (Exception e) {
            return new MeetingBook();
        }
    }
    // @@author

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}

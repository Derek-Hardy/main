package seedu.meeting.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import seedu.meeting.commons.exceptions.IllegalValueException;
import seedu.meeting.model.MeetingBook;
import seedu.meeting.model.ReadOnlyMeetingBook;
import seedu.meeting.model.group.Group;
import seedu.meeting.model.group.exceptions.GroupNotFoundException;
import seedu.meeting.model.person.Person;

/**
 * An Immutable MeetingBook that is serializable to XML format
 */
@XmlRootElement(name = "meetingbook")
public class XmlSerializableMeetingBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_GROUP = "Groups list contains duplicate group(s).";

    @XmlElement
    private List<XmlAdaptedPerson> persons;

    @XmlElement
    private List<XmlAdaptedGroup> groups;

    /**
     * Creates an empty XmlSerializableMeetingBook.
     * This empty constructor is required for marshalling.
     */
    public XmlSerializableMeetingBook() {
        persons = new ArrayList<>();
        groups = new ArrayList<>();
    }

    /**
     * Conversion
     */
    public XmlSerializableMeetingBook(ReadOnlyMeetingBook src) {
        this();
        persons.addAll(src.getPersonList().stream().map(XmlAdaptedPerson::new).collect(Collectors.toList()));
        groups.addAll(src.getGroupList().stream().map(XmlAdaptedGroup::new).collect(Collectors.toList()));
    }

    /**
     * Converts this MeetingBook into the model's {@code MeetingBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated or duplicates in the
     * {@code XmlAdaptedPerson}.
     */
    public MeetingBook toModelType() throws IllegalValueException {
        MeetingBook meetingBook = new MeetingBook();
        for (XmlAdaptedPerson p : persons) {
            Person person = p.toModelType();
            if (meetingBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            meetingBook.addPerson(person);
        }
        for (XmlAdaptedGroup g : groups) {
            Group group = g.toModelType();
            if (meetingBook.hasGroup(group)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_GROUP);
            }
            for (Person personInGroup : group.getMembersView()) {
                if (!meetingBook.hasPerson(personInGroup)) {
                    throw new GroupNotFoundException();
                }
            }
            meetingBook.addGroup(group);
        }
        return meetingBook;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlSerializableMeetingBook)) {
            return false;
        }
        return (persons.equals(((XmlSerializableMeetingBook) other).persons)
                && groups.equals(((XmlSerializableMeetingBook) other).groups));
    }
}

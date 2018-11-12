package seedu.meeting.model;

import static java.util.Objects.requireNonNull;
import static seedu.meeting.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;

import seedu.meeting.model.group.Group;
import seedu.meeting.model.group.UniqueGroupList;
import seedu.meeting.model.group.exceptions.GroupHasNoMeetingException;
import seedu.meeting.model.group.exceptions.GroupNotFoundException;
import seedu.meeting.model.meeting.Meeting;
import seedu.meeting.model.meeting.UniqueMeetingList;
import seedu.meeting.model.person.Name;
import seedu.meeting.model.person.Person;
import seedu.meeting.model.person.UniquePersonList;
import seedu.meeting.model.person.exceptions.PersonNotFoundException;
import seedu.meeting.model.shared.Title;

/**
 * Wraps all data at the meeting-book level
 * Duplicates are not allowed (by .isSamePerson, .isSameGroup comparison)
 */
public class MeetingBook implements ReadOnlyMeetingBook {

    private final UniquePersonList persons;

    // @@author Derek-Hardy
    private final UniqueGroupList groups;

    private final UniqueMeetingList meetings;

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        persons = new UniquePersonList();
        groups = new UniqueGroupList();
        meetings = new UniqueMeetingList();
    }
    // @@author

    public MeetingBook() {}

    /**
     * Creates an MeetingBook using the Persons in the {@code toBeCopied}
     */
    public MeetingBook(ReadOnlyMeetingBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    // @@author Derek-Hardy
    /**
     * Replaces the contents of the group list with {@code groups}.
     * {@code groups} must not contain duplicate groups.
     */
    public void setGroups(List<Group> groups) {
        this.groups.setGroups(groups);
        this.meetings.setMeetings(this.groups.getAllMeetings());
        for (Group g : groups) {
            for (Person p : g.getMembers().asUnmodifiableObservableList()) {
                Person toJoin = this.persons.getPersonByName(p.getName());
                if (toJoin != null && !toJoin.hasGroup(g)) {
                    toJoin.addGroup(g);
                    this.persons.setPerson(p, toJoin);
                }
            }
        }
    }
    // @@author

    /**
     * Resets the existing data of this {@code MeetingBook} with {@code newData}.
     */
    public void resetData(ReadOnlyMeetingBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setGroups(newData.getGroupList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the MeetingBook.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.hasEquivalentPerson(person);
    }

    // @@author Derek-Hardy
    /**
     * Returns true if a group with the same identity as {@code group} exists in the MeetingBook.
     */
    public boolean hasGroup(Group group) {
        requireNonNull(group);
        return groups.contains(group);
    }

    /**
     * Returns a group that matches the given title. {@code null} is returned if the group is not found.
     */
    public Group getGroupByTitle(Title title) {
        requireNonNull(title);
        return groups.getGroupByTitle(title);
    }

    /**
     * Returns a person that matches the given name. {@code null} is returned if the person is not found.
     */
    public Person getPersonByName(Name name) {
        requireNonNull(name);
        return persons.getPersonByName(name);
    }
    // @@author

    /**
     * Adds a person to the MeetingBook.
     * The person must not already exist in the MeetingBook.
     *
     * Note: The tag field is deprecated. When indicating a group, use the
     * Group class instead
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    // @@author Derek-Hardy
    /**
     * Adds a group to the MeetingBook.
     * The group must not already exist in the MeetingBook.
     */
    public void addGroup(Group group) {
        groups.add(group);
        meetings.setMeetings(groups.getAllMeetings());
        for (Person p : group.getMembersView()) {
            Person edited = persons.getPersonByName(p.getName());
            if (!edited.hasGroup(group)) {
                edited.addGroup(group);
                persons.setPerson(p, edited);
            }
        }
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the MeetingBook.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the MeetingBook.
     *
     */
    public void updatePerson(Person target, Person editedPerson) throws PersonNotFoundException {
        requireNonNull(editedPerson);

        // clear membership of target & set up membership for editedPerson
        target.clearMembership();
        editedPerson.setUpMembership();

        persons.setPerson(target, editedPerson);
    }

    /**
     * Replace the given group {@code target} in the list with {@code editedGroup}.
     * {@code target} must exist in the MeetingBook.
     * The group identity of {@code editedGroup} must not be the same as another existing group in the MeetingBook.
     *
     */
    public void updateGroup(Group target, Group editedGroup) throws GroupNotFoundException {
        requireNonNull(editedGroup);
        target.clearMembers();
        editedGroup.setUpMembers();

        groups.setGroup(target, editedGroup);
        meetings.setMeetings(groups.getAllMeetings());
    }

    /**
     * Removes {@code key} from this {@code MeetingBook}.
     * {@code key} must exist in the MeetingBook.
     */
    public void removePerson(Person key) {
        requireNonNull(key);
        key.clearMembership();
        persons.remove(key);
    }

    /**
     * Removes {@code group} from this {@code MeetingBook}.
     * {@code group} must exist in the MeetingBook.
     *
     */
    public void removeGroup(Group group) {
        requireNonNull(group);
        group.clearMembers();
        groups.remove(group);
        meetings.setMeetings(groups.getAllMeetings());
    }

    /**
     * Join a person in the {@code MeetingBook} to an existing {@code group} in the {@code MeetingBook}.
     */
    public void joinGroup(Person person, Group group) {
        requireAllNonNull(person, group);
        Person personCopy = person.copy();
        Group groupCopy = group.copy();

        group.addMember(person);

        updatePerson(personCopy, person);
        updateGroup(groupCopy, group);
    }

    /**
     * Remove an existing member from a existing {@code group} in {@code MeetingBook}.
     */
    public void leaveGroup(Person person, Group group) {
        requireAllNonNull(person, group);
        Person personCopy = person.copy();
        Group groupCopy = group.copy();

        group.removeMember(person);

        updatePerson(personCopy, person);
        updateGroup(groupCopy, group);
    }

    // @@author NyxF4ll
    /**
     * Sets meeting field of {@code group} in the group list to {@code meeting}.
     */
    public void setMeeting(Group group, Meeting meeting) throws GroupNotFoundException {
        groups.setMeeting(group, meeting);
        meetings.setMeetings(groups.getAllMeetings());
    }

    /**
     * Resets meeting field of {@code group} in the group list to an empty optional.
     */
    public void cancelMeeting(Group group) throws GroupNotFoundException, GroupHasNoMeetingException {
        groups.cancelMeeting(group);
        meetings.setMeetings(groups.getAllMeetings());
    }
    // @@author

    //// util methods

    @Override
    public String toString() {
        return persons.asUnmodifiableObservableList().size() + " persons, "
                + groups.asUnmodifiableObservableList().size() + " groups, "
                + meetings.asUnmodifiableObservableList().size() + " meetings";
        // TODO: refine later
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    // @@author Derek-Hardy
    @Override
    public ObservableList<Group> getGroupList() {
        return groups.asUnmodifiableObservableList();
    }
    // @@author


    @Override
    public ObservableList<Meeting> getMeetingList() {
        return meetings.asUnmodifiableObservableList();
    }

    // @@author Zenious
    /**
     * merges the {@code importedPerson} into the MeetingBook with regards to the {@code overwrite} flag.
     */
    private void mergePerson(Person importedPerson, boolean overwrite) {
        if (!hasPerson(importedPerson)) {
            addPerson(importedPerson);
        } else if (overwrite) {
            Person samePerson = getPersonByName(importedPerson.getName());
            updatePerson(samePerson, importedPerson);
        }
    }
    // @@author NyxF4ll

    /**
     * merges the {@code importedGroup} into the MeetingBook with regards to the {@code overwrite} flag.
     */
    private void mergeGroup(Group importedGroup, boolean overwrite) {
        importedGroup.getMembersView()
                .stream()
                .filter((p) -> !persons.contains(p))
                .forEach(importedGroup::removeMemberNoGroups);

        // @@author Zenious
        if (!hasGroup(importedGroup)) {
            addGroup(importedGroup);
        } else if (overwrite) {
            Group sameGroup = getGroupByTitle(importedGroup.getTitle());
            updateGroup(sameGroup, importedGroup);

        }
    }

    /**
     * Merge another MeetingBook into current MeetingBook.
     *
     */
    public void merge(ReadOnlyMeetingBook imported, boolean overwrite) {
        // If Both book contains same entries
        if (equals(imported)) {
            return;
        }

        if (!(imported instanceof MeetingBook)) {
            return;
        }
        // @@author NyxF4ll
        MeetingBook importedBook = (MeetingBook) imported;
        importedBook.persons.iterator()
                .forEachRemaining((person) -> mergePerson(person, overwrite));

        importedBook.groups.iterator()
                .forEachRemaining((group) -> mergeGroup(group, overwrite));
    }
    // @@author

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof MeetingBook // instanceof handles nulls
                && persons.equals(((MeetingBook) other).persons))
                && groups.equals(((MeetingBook) other).groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, groups);
    }
}

package seedu.meeting.model.group;

import static java.util.Objects.requireNonNull;
import static seedu.meeting.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.meeting.model.group.exceptions.GroupHasNoMeetingException;
import seedu.meeting.model.meeting.Meeting;
import seedu.meeting.model.person.Person;
import seedu.meeting.model.person.UniquePersonList;
import seedu.meeting.model.shared.Description;
import seedu.meeting.model.shared.Title;

// @@author Derek-Hardy
/**
 * Represents a Group for participants in the MeetingBook.
 * The person will be able to keep a list of groups that he/she has enrolled.
 * The group will also keep track of a list of its members.
 * Every group will contain one and only one meeting. The newly added meeting will always
 * overwrite the old one.
 *
 * Meeting can be {@code null} if no meeting is required for members of this group.
 *
 * Guarantees: title, description and UniquePersonList are present and not null,
 * field values are validated, immutable.
 *
 * {@author Derek-Hardy}
 */
public class Group {

    // Identity fields
    private final Title title;
    private final Optional<Description> description;

    // Data fields
    private Optional<Meeting> meeting;
    private final UniquePersonList members;

    /**
     * Constructor for a simple group with user-input title.
     * Description, meeting and members fields are initialised to be empty.
     *
     * Title is guaranteed to be present and non-null.
     *
     * @param title The name of the group
     */
    public Group(Title title) {
        requireNonNull(title);
        this.title = title;
        this.description = Optional.empty();
        this.meeting = Optional.empty();
        this.members = new UniquePersonList();
    }

    /**
     * Constructor for a simple group, where title, description and meeting
     * must be present. Members field is initialised to be empty.
     *
     * @param title The name of the group
     * @param description The description of the group or the agenda of meeting
     * @param meeting The upcoming meeting for people in this group
     */
    public Group(Title title, Description description, Meeting meeting) {
        requireAllNonNull(title, meeting, description);
        this.title = title;
        this.description = Optional.of(description);
        this.meeting = Optional.of(meeting);
        this.members = new UniquePersonList();
    }

    /**
     * Constructor for a simple group, where title, meeting and members
     * must be present. Description field is initialised to be empty.
     *
     * @param title The name of the group
     * @param meeting The upcoming meeting for people in this group
     * @param members The list of members of the group
     */
    public Group(Title title, Meeting meeting, UniquePersonList members) {
        requireAllNonNull(title, meeting, members);
        this.title = title;
        this.description = Optional.empty();
        this.meeting = Optional.of(meeting);
        this.members = new UniquePersonList();
        this.members.setPersons(members);
    }

    /**
     * Constructor for a simple group with meeting initialised to be empty.
     * Members field is initialised to an existing list of members.
     *
     * @param title The name of the group
     * @param description The description of the group or the agenda of meeting
     * @param members The list of members of the group
     */
    public Group(Title title, Description description, UniquePersonList members) {
        requireAllNonNull(title, description, members);
        this.title = title;
        this.description = Optional.of(description);
        this.meeting = Optional.empty();
        this.members = new UniquePersonList();
        this.members.setPersons(members);
    }

    /**
     * Constructor for a full description of group. All fields must be present and non-null.
     * Members field is initialised to an existing list of members.
     *
     * @param title The name of the group
     * @param description The description of the group or the agenda of meeting
     * @param meeting The upcoming meeting for people in this group
     * @param members The list of members of the group
     */
    public Group(Title title, Optional<Description> description, Optional<Meeting> meeting,
                 UniquePersonList members) {
        requireAllNonNull(title, meeting, description, members);
        this.title = title;
        this.description = description;
        this.meeting = meeting;
        this.members = new UniquePersonList();
        this.members.setPersons(members);
    }

    // @@author jeffreyooi
    /**
     * Copy constructor
     */
    public Group(Group toCopy) {
        this.title = toCopy.getTitle();
        this.description = Optional.ofNullable(toCopy.getDescription());
        this.meeting = Optional.ofNullable(toCopy.getMeeting());
        this.members = new UniquePersonList();
        this.members.setPersons(toCopy.getMembers());
    }
    // @@author

    public Title getTitle() {
        return title;
    }

    public Description getDescription() {
        return description.orElse(null);
    }

    public Meeting getMeeting() {
        return meeting.orElse(null);
    }

    // @@author NyxF4ll

    /**
     * Returns true if the group has a meeting associated with it.
     */
    public boolean hasMeeting() {
        return meeting.isPresent();
    }
    /**
     * Set this group's meeting to {@code meeting}.
     */
    public void setMeeting(Meeting meeting) {
        requireNonNull(meeting);
        this.meeting = Optional.of(meeting);
    }

    /**
     * Set this group's meeting to be an empty optional.
     */
    public void cancelMeeting() throws GroupHasNoMeetingException {
        if (!this.meeting.isPresent()) {
            throw new GroupHasNoMeetingException();
        }

        this.meeting = Optional.empty();
    }
    // @@author

    /**
     * Return a UniquePersonList view of all members in this group.
     */
    public UniquePersonList getMembers() {
        UniquePersonList toList = new UniquePersonList();
        toList.setPersons(members);
        return toList;
    }

    /**
     * Returns an immutable list of members in this group, which
     * throws {@code UnsupportedOperationException} if modification
     * is attempted.
     */
    public List<Person> getMembersView() {
        return Collections.unmodifiableList(members.asUnmodifiableObservableList());
    }

    /**
     * Returns true if the {@code person} is in this group.
     * @param person The person to check membership
     */
    public boolean hasMember(Person person) {
        return this.members.contains(person);
    }

    /**
     * Add a person to be the member of this group.
     * The person will be updated with the new group relation. (bi-directional)
     */
    public void addMember(Person toAdd) {
        requireNonNull(toAdd);
        this.members.add(toAdd);

        if (!toAdd.hasGroup(this)) {
            toAdd.addGroup(this);
        }
    }

    /**
     * Remove a person from this group.
     * The person will be updated with the new group relation. (bi-directional)
     */
    public void removeMember(Person toRemove) {
        requireNonNull(toRemove);
        this.members.remove(toRemove);

        toRemove.removeGroupHelper(this);
    }

    // @@author zenious
    /**
     * Remove a person from this group that is not in groups yet.
     *
     */
    public void removeMemberNoGroups(Person toRemove) {
        requireNonNull(toRemove);
        this.members.remove(toRemove);
    }
    // @@author

    /**
     * This method is reserved to be called only from {@link seedu.meeting.model.person.Person#removeGroup(Group)}
     * and {@link Person#clearMembership()} methods.
     */
    public void removeMemberHelper(Person person) {
        this.members.remove(person);
    }

    /**
     * Remove all the members in this group.
     * The people will be updated with the new group relation. (bi-directional)
     */
    public void clearMembers() {
        // enhanced for loop to remove the group from person
        for (Person member : this.members) {
            if (member.hasGroup(this)) {
                member.removeGroupHelper(this);
            }
        }
        this.members.clear();
    }

    /**
     * Set up the member connections for this group.
     * The people will be updated with the new group relation. (bi-directional)
     */
    public void setUpMembers() {
        // enhanced for loop to set up the member connection of this group
        for (Person member : this.members) {
            if (!member.hasGroup(this)) {
                member.addGroup(this);
            }
        }
    }

    /**
     * Create a copy of this group.
     * The returned copy should return {@code true} for method {@code equals()} with the original group.
     */
    public Group copy() {
        return new Group(title, description, meeting, members);
    }

    /**
     * Returns true if both groups have the same title.
     * This defines a weaker notion of equality between two groups.
     */
    public boolean isSameGroup(Group otherGroup) {
        if (otherGroup == this) {
            return true;
        }

        return otherGroup != null
                && otherGroup.getTitle().equals(getTitle());
    }

    /**
     * Returns true if both groups have the same identity and data fields.
     * This defines a stronger notion of equality between two groups.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Group)) {
            return false;
        }

        Group otherGroup = (Group) other;
        return otherGroup.getTitle().equals(getTitle())
                && otherGroup.description.equals(this.description)
                && otherGroup.meeting.equals(this.meeting);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(title, meeting, description, members);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        Meeting meeting = getMeeting();

        builder.append(getTitle() + "\n");

        if (meeting != null) {
            builder.append("Next meeting details: ")
                   .append(meeting + "\n");
        } else {
            builder.append("\n");
        }

        return builder.toString();
    }

}

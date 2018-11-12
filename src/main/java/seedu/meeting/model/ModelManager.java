package seedu.meeting.model;

import static java.util.Objects.requireNonNull;
import static seedu.meeting.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.meeting.commons.core.ComponentManager;
import seedu.meeting.commons.core.LogsCenter;
import seedu.meeting.commons.events.model.MeetingBookChangedEvent;
import seedu.meeting.commons.events.model.MeetingBookExportEvent;
import seedu.meeting.commons.events.model.UserPrefsChangeEvent;
import seedu.meeting.commons.events.ui.RefreshGroupListEvent;
import seedu.meeting.model.group.Group;
import seedu.meeting.model.group.exceptions.GroupHasNoMeetingException;
import seedu.meeting.model.group.exceptions.GroupNotFoundException;
import seedu.meeting.model.meeting.Meeting;
import seedu.meeting.model.person.Name;
import seedu.meeting.model.person.Person;
import seedu.meeting.model.person.util.PersonPropertyComparator;
import seedu.meeting.model.shared.Title;


/**
 * Represents the in-memory model of the MeetingBook data.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedMeetingBook versionedMeetingBook;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Group> filteredGroups;
    private final FilteredList<Meeting> filteredMeetings;
    private final UserPrefs userPrefs;
    private final SortedList<Person> sortedPersons;

    /**
     * Initializes a ModelManager with the given meetingBook and userPrefs.
     */
    public ModelManager(ReadOnlyMeetingBook meetingBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(meetingBook, userPrefs);

        logger.fine("Initializing with MeetingBook: " + meetingBook + " and user prefs " + userPrefs);

        versionedMeetingBook = new VersionedMeetingBook(meetingBook);
        filteredPersons = new FilteredList<>(versionedMeetingBook.getPersonList());
        filteredGroups = new FilteredList<>(versionedMeetingBook.getGroupList());
        filteredMeetings = new FilteredList<>(versionedMeetingBook.getMeetingList());
        this.userPrefs = userPrefs;
        sortedPersons = new SortedList<>(filteredPersons);
    }

    public ModelManager() {
        this(new MeetingBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyMeetingBook newData) {
        versionedMeetingBook.resetData(newData);
        indicateMeetingBookChanged();
    }

    @Override
    public ReadOnlyMeetingBook getMeetingBook() {
        return versionedMeetingBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateMeetingBookChanged() {
        raise(new MeetingBookChangedEvent(versionedMeetingBook));
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return versionedMeetingBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        versionedMeetingBook.removePerson(target);
        raise(new RefreshGroupListEvent());
        indicateMeetingBookChanged();
    }

    @Override
    public void addPerson(Person person) {
        versionedMeetingBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateMeetingBookChanged();
    }

    // @@author Derek-Hardy
    @Override
    public void updatePerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        versionedMeetingBook.updatePerson(target, editedPerson);
        indicateMeetingBookChanged();
    }

    @Override
    public void updateGroup(Group target, Group editedGroup) throws GroupNotFoundException {
        requireAllNonNull(target, editedGroup);

        versionedMeetingBook.updateGroup(target, editedGroup);
        indicateMeetingBookChanged();
    }
    // @@author

    //=========== AddGroup / RemoveGroup =====================================================================

    // @@author Derek-Hardy
    @Override
    public void addGroup(Group group) {
        requireNonNull(group);
        versionedMeetingBook.addGroup(group);
        indicateMeetingBookChanged();
    }

    @Override
    public void removeGroup(Group group) {
        requireNonNull(group);
        versionedMeetingBook.removeGroup(group);
        indicateMeetingBookChanged();
    }

    @Override
    public void joinGroup(Person person, Group group) {
        requireAllNonNull(person, group);
        versionedMeetingBook.joinGroup(person, group);
        showAllListEntries();
        indicateMeetingBookChanged();
    }

    @Override
    public void leaveGroup(Person person, Group group) {
        requireAllNonNull(person, group);
        versionedMeetingBook.leaveGroup(person, group);
        showAllListEntries();
        indicateMeetingBookChanged();
    }

    // @@author

    // @@author jeffreyooi
    /**
     * Filters all lists to show all entries.
     */
    private void showAllListEntries() {
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        updateFilteredMeetingList(PREDICATE_SHOW_ALL_MEETINGS);
        updateFilteredGroupList(PREDICATE_SHOW_ALL_GROUPS);
    }
    // @@author

    // @@author NyxF4ll
    @Override
    public boolean hasGroup(Group group) {
        return versionedMeetingBook.getGroupList().stream().anyMatch(group::isSameGroup);
    }

    @Override
    public ObservableList<Group> getGroupList() {
        return versionedMeetingBook.getGroupList();
    }
    // @@author

    @Override
    public Group getGroupByTitle(Title title) {
        return versionedMeetingBook.getGroupByTitle(title);
    }

    @Override
    public Person getPersonByName(Name name) {
        return versionedMeetingBook.getPersonByName(name);
    }

    //=========== Meetings ===================================================================================

    // @@author NyxF4ll
    @Override
    public void setMeeting(Group group, Meeting meeting) throws GroupNotFoundException {
        versionedMeetingBook.setMeeting(group, meeting);
        raise(new RefreshGroupListEvent());
        indicateMeetingBookChanged();
    }

    @Override
    public void cancelMeeting(Group group) throws GroupNotFoundException, GroupHasNoMeetingException {
        versionedMeetingBook.cancelMeeting(group);
        raise(new RefreshGroupListEvent());
        indicateMeetingBookChanged();
    }
    // @@author

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedMeetingBook}
     */
    @Override
    @Deprecated // Use getSortedPersonList() to obtain the list for display.
    public ObservableList<Person> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== Filtered Group List Accessors =============================================================

    @Override
    public ObservableList<Group> getFilteredGroupList() {
        return FXCollections.unmodifiableObservableList(filteredGroups);
    }

    @Override
    public void updateFilteredGroupList(Predicate<Group> predicate) {
        requireNonNull(predicate);
        filteredGroups.setPredicate(predicate);
    }

    // @@author betakuwe
    //=========== Sorted Person List Accessors ==============================================================

    @Override
    public ObservableList<Person> getSortedPersonList() {
        return FXCollections.unmodifiableObservableList(sortedPersons);
    }

    @Override
    public void updateSortedPersonList(PersonPropertyComparator personPropertyComparator) {
        requireNonNull(personPropertyComparator);
        sortedPersons.setComparator(personPropertyComparator.getComparator());
    }
    // @@author

    @Override
    public ObservableList<Meeting> getFilteredMeetingList() {
        return FXCollections.unmodifiableObservableList(filteredMeetings);
    }

    @Override
    public void updateFilteredMeetingList(Predicate<Meeting> predicate) {
        requireNonNull(predicate);
        filteredMeetings.setPredicate(predicate);
    }

    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoMeetingBook() {
        return versionedMeetingBook.canUndo();
    }

    @Override
    public boolean canRedoMeetingBook() {
        return versionedMeetingBook.canRedo();
    }

    @Override
    public void undoMeetingBook() {
        versionedMeetingBook.undo();
        indicateMeetingBookChanged();
    }

    @Override
    public void redoMeetingBook() {
        versionedMeetingBook.redo();
        indicateMeetingBookChanged();
    }

    @Override
    public void commitMeetingBook() {
        versionedMeetingBook.commit();
    }

    // ================= Export/Import =======================================================================

    @Override
    public void exportMeetingBook(Path filepath) {
        raise(new MeetingBookExportEvent(versionedMeetingBook, filepath));
    }

    @Override
    public Path getMeetingBookFilePath() {
        return userPrefs.getMeetingBookFilePath();
    }

    @Override
    public void changeUserPrefs(Path filepath) {
        Path currentPath = userPrefs.getMeetingBookFilePath();
        userPrefs.setMeetingBookFilePath(filepath);
        raise(new UserPrefsChangeEvent(userPrefs, versionedMeetingBook, currentPath, filepath));
    }

    @Override
    public void importMeetingBook(ReadOnlyMeetingBook importedMeetingBook, boolean overwrite) {
        versionedMeetingBook.merge(importedMeetingBook, overwrite);
        indicateMeetingBookChanged();
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedMeetingBook.equals(other.versionedMeetingBook)
                && sortedPersons.equals(other.sortedPersons);
    }

}

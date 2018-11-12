package seedu.meeting.model.group;

import static java.util.Objects.requireNonNull;
import static seedu.meeting.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import seedu.meeting.model.group.exceptions.DuplicateGroupException;
import seedu.meeting.model.group.exceptions.GroupHasNoMeetingException;
import seedu.meeting.model.group.exceptions.GroupNotFoundException;
import seedu.meeting.model.meeting.Meeting;
import seedu.meeting.model.shared.Title;


/**
 * A list of groups that enforces uniqueness between its elements and does not allow {@code null}.
 *
 * A list is considered unique by comparing using {@code Group#isSameGroup(Group)}. As such, adding and updating of
 * groups uses Group#isSameGroup(Group) for equality so as to ensure that the group being added or updated is
 * unique in terms of identity in the UniqueGroupList. However, the removal of a group uses Group#equals(Object) so
 * as to ensure that the group with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Group#isSameGroup(Group)
 *
 * {@author Derek-Hardy}
 */
// @@author Derek-Hardy
public class UniqueGroupList implements Iterable<Group> {

    private final ObservableList<Group> internalList = FXCollections.observableArrayList();

    /**
     * Returns true if the list contains an equivalent group as the given argument.
     */
    public boolean contains(Group toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameGroup);
    }

    /**
     * Empty the list of groups.
     */
    public void clear() {
        internalList.clear();
    }

    /**
     * Adds a group to the list.
     * The group must not already exist in the list.
     */
    public void add(Group toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateGroupException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the group {@code target} in the list with {@code editedGroup}.
     * {@code target} must exist in the list.
     * The group identity of {@code editedGroup} must not be the same as another existing group in the list.
     */
    public void setGroup(Group target, Group editedGroup) {
        requireAllNonNull(target, editedGroup);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new GroupNotFoundException();
        }

        if (!target.isSameGroup(editedGroup) && contains(editedGroup)) {
            throw new DuplicateGroupException();
        }

        internalList.set(index, editedGroup);
    }

    /**
     * Removes the equivalent group from the list.
     * The group must exist in the list.
     */
    public void remove(Group toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new GroupNotFoundException();
        }
    }

    /**
     * Replace all the groups in this UniqueGroupList with a new set of groups.
     */
    public void setGroups(UniqueGroupList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code groups}.
     * {@code groups} must not contain duplicate groups.
     */
    public void setGroups(List<Group> groups) {
        requireAllNonNull(groups);
        if (!groupsAreUnique(groups)) {
            throw new DuplicateGroupException();
        }

        internalList.setAll(groups);
    }

    // @@author NyxF4ll
    /**
     * Sets meeting field of {@code group} in the group list to {@code meeting}.
     */
    public void setMeeting(Group group, Meeting meeting) throws GroupNotFoundException {
        Optional<Group> target = internalList.stream().filter(group::isSameGroup).findAny();
        if (!target.isPresent()) {
            throw new GroupNotFoundException();
        }
        Group editedGroup = target.get().copy();
        editedGroup.setMeeting(meeting);
        setGroup(target.get(), editedGroup);
    }

    /**
     * Resets meeting field of {@code group} in the group list to an empty optional.
     */
    public void cancelMeeting(Group group) throws GroupNotFoundException, GroupHasNoMeetingException {
        Optional<Group> target = internalList.stream().filter(group::isSameGroup).findAny();
        if (!target.isPresent()) {
            throw new GroupNotFoundException();
        }
        Group editedGroup = target.get().copy();
        editedGroup.cancelMeeting();
        setGroup(target.get(), editedGroup);
    }

    // @@author jeffreyooi
    /**
     * Returns a list of meetings from group.
     */
    public List<Meeting> getAllMeetings() {
        return internalList.stream().filter(g -> g.getMeeting() != null).map(Group::getMeeting)
            .collect(Collectors.toList());
    }
    // @@author

    // @@author Derek-Hardy
    /**
     * Find an existing group that matches the given {@code title}.
     * If the group is found, returns the matched group. Else returns {@code null}.
     */
    public Group getGroupByTitle(Title title) {
        requireNonNull(title);

        for (Group group : internalList) {
            Title groupTitle = group.getTitle();
            if (title.equals(groupTitle)) {
                return group.copy();
            }
        }
        // if no match, returns null
        return null;
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Group> asUnmodifiableObservableList() {
        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<Group> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueGroupList // instanceof handles nulls
                && internalList.equals(((UniqueGroupList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code groups} contains only unique groups.
     */
    private boolean groupsAreUnique(List<Group> groups) {

        for (int i = 0; i < groups.size() - 1; i++) {
            for (int j = i + 1; j < groups.size(); j++) {
                if (groups.get(i).isSameGroup(groups.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}

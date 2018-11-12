package seedu.meeting.model.group;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.meeting.testutil.TypicalGroups.GROUP_2101;
import static seedu.meeting.testutil.TypicalGroups.PROJECT_2103T;
import static seedu.meeting.testutil.TypicalMeetings.URGENT;
import static seedu.meeting.testutil.TypicalPersons.ALICE;
import static seedu.meeting.testutil.TypicalPersons.BOB;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;

import seedu.meeting.model.person.Person;
import seedu.meeting.model.person.UniquePersonList;
import seedu.meeting.testutil.GroupBuilder;
import seedu.meeting.testutil.PersonBuilder;

// @@author Derek-Hardy
/**
 * {@author Derek-Hardy}
 */
public class GroupTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void asUnmodifiableList_modifyList_throwsUnsupportedOperationException() {
        Group group = new GroupBuilder().build();
        thrown.expect(UnsupportedOperationException.class);
        group.getMembersView().remove(0);
    }

    @Test
    public void getMembers_memberInGroup_returnsTrue() {
        Group group = new GroupBuilder().withNewPerson(ALICE).build();
        UniquePersonList list = group.getMembers();

        assertTrue(list.hasEquivalentPerson(ALICE));
    }

    @Test
    public void hasMember_personNotInGroup_returnsFalse() {
        Group group = new GroupBuilder().build();
        assertFalse(group.hasMember(ALICE));
    }

    @Test
    public void addMember_personAddedInGroup_returnsTrue() {
        Group group = new GroupBuilder().build();
        group.addMember(ALICE);
        assertTrue(group.hasMember(ALICE));
    }

    @Test
    public void removeMember_personRemovedFromGroup_returnsFalse() {
        Group group = new GroupBuilder().build();
        Person person = new PersonBuilder().withGroup(group).build();
        // since bidirectional relation is not possible in GroupBuilder
        // manual addition is needed here
        group.addMember(person);

        group.removeMember(person);
        assertFalse(group.hasMember(person));
    }

    @Test
    public void removeMemberNoGroups_personRemovedFromGroup_returnsFalse() {
        Person person = new PersonBuilder().build();
        Group group = new GroupBuilder().withNewPerson(person).build();

        group.removeMemberNoGroups(person);
        assertFalse(group.hasMember(person));
    }

    @Test
    public void removeMemberHelper_personNotInGroup_returnsFalse() {
        Group group = new GroupBuilder().withNewPerson(ALICE).build();
        group.removeMemberHelper(ALICE);

        assertFalse(group.hasMember(ALICE));
    }

    @Test
    public void clearMembers_personNotInGroup_returnsFalse() {
        Person derrick = new PersonBuilder().withName("Derrick").build();
        Person peter = new PersonBuilder().withName("Peter").build();
        Group group = new GroupBuilder().withNewPerson(derrick).withNewPerson(peter).build();
        // since bidirectional relation is not possible in PersonBuilder
        // manual addition is needed here
        derrick.addGroup(group);
        peter.addGroup(group);

        group.clearMembers();
        assertFalse(group.hasMember(derrick));
    }

    @Test
    public void setUpMembers_personInGroup_returnsTrue() {
        Person person = new PersonBuilder().build();
        // since bidirectional relation is not possible in PersonBuilder
        // manual addition is needed here
        Group group = new GroupBuilder().withNewPerson(person).build();
        person.addGroup(group);

        person.removeGroupHelper(group);
        group.setUpMembers();
        assertTrue(person.hasGroup(group));
    }

    @Test
    public void copy_equals_returnsTrue() {
        Group group = new GroupBuilder().withTitle("copy").withDescription("This is used to be copied").build();
        Group groupCopy = group.copy();

        assertTrue(groupCopy.equals(group));
    }

    @Test
    public void isSameGroup() {
        // same object -> returns true
        assertTrue(PROJECT_2103T.isSameGroup(PROJECT_2103T));

        // null -> returns false
        assertFalse(PROJECT_2103T.isSameGroup(null));

        // different title -> returns false
        Group editedProject = new GroupBuilder(PROJECT_2103T).withTitle("CS2113").build();
        assertFalse(editedProject.isSameGroup(PROJECT_2103T));

        // different description -> returns true
        editedProject = new GroupBuilder(PROJECT_2103T).withDescription("Discussion group for CS2103T").build();
        assertTrue(editedProject.isSameGroup(PROJECT_2103T));

        // same title, same description, different meeting -> returns true
        editedProject = new GroupBuilder(PROJECT_2103T).withMeeting(URGENT).build();
        assertTrue(editedProject.isSameGroup(PROJECT_2103T));

        // same title, same description, different members -> returns true
        editedProject = new GroupBuilder(PROJECT_2103T).withNewPerson(BOB).build();
        assertTrue(editedProject.isSameGroup(PROJECT_2103T));

        // same members -> returns true
        editedProject = new GroupBuilder(PROJECT_2103T).withNewPerson(BOB).withRemovedPerson(BOB).build();
        assertTrue(editedProject.isSameGroup(PROJECT_2103T));
    }


    @Test
    public void equals() {
        // same values -> returns true
        Group projectCopy = new GroupBuilder(GROUP_2101).build();
        assertTrue(GROUP_2101.equals(projectCopy));

        // same object -> returns true
        assertTrue(GROUP_2101.equals(GROUP_2101));

        // null -> returns false
        assertFalse(GROUP_2101.equals(null));

        // different type -> returns false
        assertFalse(GROUP_2101.equals(2));

        // different groups -> returns false
        assertFalse(GROUP_2101.equals(PROJECT_2103T));

        // different title -> returns false
        Group editedGroup = new GroupBuilder(GROUP_2101).withTitle("CS2102").build();
        assertFalse(editedGroup.equals(GROUP_2101));

        // different description -> returns false
        editedGroup = new GroupBuilder(GROUP_2101).withDescription("Discussion group for CS2103T").build();
        assertFalse(editedGroup.equals(GROUP_2101));

        // same title, same description, different meeting -> returns false
        editedGroup = new GroupBuilder(GROUP_2101).withMeeting(URGENT).build();
        assertFalse(editedGroup.equals(GROUP_2101));

        // same title, same description, different members -> returns true
        editedGroup = new GroupBuilder(GROUP_2101).withNewPerson(BOB).build();
        assertTrue(editedGroup.equals(GROUP_2101));

        // same members -> returns true
        editedGroup = new GroupBuilder(GROUP_2101).withNewPerson(BOB).withRemovedPerson(BOB).build();
        assertTrue(editedGroup.equals(GROUP_2101));
    }
}

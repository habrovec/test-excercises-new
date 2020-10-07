package tests.group;

import com.BaseTest;
import com.data.GroupData;
import com.helpers.product.GroupHelper;
import com.utils.ModifiedSortedList;
import org.testng.annotations.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class GroupModificationTest extends BaseTest {

    @Test(dataProvider = "randomValidGroupGenerator")
    public void deleteGroup(GroupData group) {
        GroupHelper groupHelper = browserController.getGroupHelper();
        // save old state
        ModifiedSortedList<GroupData> oldList = groupHelper.getUiGroups();
        System.out.println("old list " + oldList.size());
        Random rnd = new Random();
        int index = rnd.nextInt(oldList.size() - 1);

        groupHelper.modifyGroup(index, group);
        // save new state
        ModifiedSortedList<GroupData> newList = groupHelper.getUiGroups();
        System.out.println("newList list " + newList.size());
        // compare items in the lists
        oldList.remove(index);
        oldList.add(group);
        //Collections.sort(oldList);
        //assertEquals(newList,oldList);
        assertThat(newList, equalTo(oldList.without(index)));
    }
}


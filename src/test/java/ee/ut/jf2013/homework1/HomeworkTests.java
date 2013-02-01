package ee.ut.jf2013.homework1;

import static org.junit.Assert.*;
import org.junit.Test;

public class HomeworkTests {

    @Test
    public void testSquare() {
        Homework homework = new Homework();
        assertEquals(16, homework.square(4));
    }

    @Test
    public void testReverse() {
        Homework homework = new Homework();
        assertEquals("Input", homework.reverse("tupnI"));
    }
}

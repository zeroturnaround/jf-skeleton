package ee.ut.jf2013.homework1;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BinaryFileReverserTest {

    private final String fileName = "testInputFile.txt";
    private final String initialFileContent = "1234567";
    private BinaryFileReverser fileReverser = new BinaryFileReverser();

    @Before
    public void setUp() throws Exception {
        createNewFileWithContent();
    }

    @After
    public void tearDown() throws Exception {
        new File(fileName).delete();
        new File(BinaryFileReverser.PREFIX + fileName).delete();
    }

    @Test
    public void testReversedFileExists() throws Exception {
        String outputFileName = fileReverser.reverseBinaryFileContent(fileName);
        assertTrue(new File(outputFileName).exists());
    }

    @Test
    public void testReversedFileContentIsCorrect() throws Exception {
        String outputFileName = fileReverser.reverseBinaryFileContent(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(outputFileName));
        String fileLine = reader.readLine();
        assertEquals(StringUtils.reverse(initialFileContent), fileLine);
        reader.close();
    }

    private void createNewFileWithContent() throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.append(initialFileContent);
        writer.close();
    }
}

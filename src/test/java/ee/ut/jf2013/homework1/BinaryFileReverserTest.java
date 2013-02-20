package ee.ut.jf2013.homework1;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BinaryFileReverserTest {

    private File inputFile;
    private String outputFileName;
    private final String initialFileContent = "1234567";
    private BinaryFileReverser fileReverser = new BinaryFileReverser();

    @Before
    public void setUp() throws Exception {
        inputFile = createNewFileWithContent();
    }

    @After
    public void tearDown() throws Exception {
        new File(outputFileName).deleteOnExit();
    }

    @Test
    public void testReversedFileExists() throws Exception {
        outputFileName = fileReverser.reverseBinaryFileContent(inputFile.getAbsolutePath());
        assertTrue(new File(outputFileName).exists());
    }

    @Test
    public void testReversedFileContentIsCorrect() throws Exception {
        outputFileName = fileReverser.reverseBinaryFileContent(inputFile.getAbsolutePath());
        BufferedReader reader = new BufferedReader(new FileReader(outputFileName));
        String fileLine = reader.readLine();
        assertEquals(StringUtils.reverse(initialFileContent), fileLine);
        reader.close();
    }

    @Test
    public void testReversedSingleFileIsCorrect() throws Exception {
        fileReverser.reverseBinaryFileContentInSingleFile(inputFile.getAbsolutePath());
        BufferedReader reader = new BufferedReader(new FileReader(inputFile.getAbsolutePath()));
        String fileLine = reader.readLine();
        assertEquals(StringUtils.reverse(initialFileContent), fileLine);
        reader.close();
    }

    private File createNewFileWithContent() throws IOException {
        File file = File.createTempFile("testFile", null, new File("."));
        file.deleteOnExit();
        FileWriter writer = new FileWriter(file);
        writer.append(initialFileContent);
        writer.close();
        return file;
    }
}

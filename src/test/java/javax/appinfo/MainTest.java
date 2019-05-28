package javax.appinfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static javax.appinfo.test.PropertiesAssert.assertPropertyDoesNotExist;
import static javax.appinfo.test.PropertiesAssert.assertPropertyExists;

/**
 * @author Jonatan Ivanov
 */
public class MainTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(out));
    }

    @After
    public void tearDown() {
        System.setOut(null);
    }

    @Test
    public void mainTest() throws Exception {
        String[] args = new String[0];
        Main.main(args);

        String content = out.toString();
        assertPropertyExists("java.version", content);
        assertPropertyExists("PATH", content);
        assertPropertyExists("runtime.specVersion", content);
        assertPropertyExists("classLoading.totalLoadedClassCount", content);
        assertPropertyExists("compilation.totalCompilationTime", content);
        assertPropertyExists("gc.0.collectionCount", content);
        assertPropertyExists("memory.heapMemoryUsage.used", content);
        assertPropertyExists("os.name", content);
        assertPropertyExists("thread.threadCount", content);
    }

    @Test
    public void mainFilterTest() throws Exception {
        String[] args = {"totalLoadedClassCount", "totalCompilationTime"};
        Main.main(args);

        String content = out.toString();
        assertPropertyExists("classLoading.totalLoadedClassCount", content);
        assertPropertyExists("compilation.totalCompilationTime", content);

        assertPropertyDoesNotExist("java.version", content);
        assertPropertyDoesNotExist("PATH", content);
        assertPropertyDoesNotExist("runtime.specVersion", content);
        assertPropertyDoesNotExist("gc.0.collectionCount", content);
        assertPropertyDoesNotExist("memory.heapMemoryUsage.used", content);
        assertPropertyDoesNotExist("os.name", content);
        assertPropertyDoesNotExist("thread.threadCount", content);
    }
}
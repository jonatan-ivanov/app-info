package javax.appinfo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static javax.appinfo.test.PropertiesAssert.assertPropertyExists;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jonatan Ivanov
 */
public class AppInfoServletTest {
    @Rule public ExpectedException thrown = ExpectedException.none();

    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse resp = mock(HttpServletResponse.class);
    private PrintWriter writer = mock(PrintWriter.class);
    private ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

    private AppInfoServlet servlet = new AppInfoServlet();

    @Test
    public void doGetTest() throws Exception {
        when(resp.getWriter()).thenReturn(writer);

        servlet.doGet(req, resp);
        verify(resp).getWriter();
        verify(writer).write(argument.capture());
        verify(writer).close();

        String content = argument.getValue();
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
    public void doGetNullResponseTest() throws Exception {
        thrown.expect(NullPointerException.class);
        servlet.doGet(req, null);
    }

    @Test
    public void doGetNullWriterTest() throws Exception {
        when(resp.getWriter()).thenReturn(null);

        thrown.expect(NullPointerException.class);

        servlet.doGet(req, resp);
        verify(resp).getWriter();
    }

    @Test
    public void doGetIOExceptionOnGetWriterTest() throws Exception {
        when(resp.getWriter()).thenThrow(new IOException("simulated IOException"));

        thrown.expect(IOException.class);
        thrown.expectMessage("simulated IOException");

        servlet.doGet(req, resp);
        verify(resp).getWriter();
    }

    @Test
    public void doGetRuntimeExceptionOnGetWriterTest() throws Exception {
        when(resp.getWriter()).thenThrow(new RuntimeException("simulated RuntimeException"));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("simulated RuntimeException");

        servlet.doGet(req, resp);
        verify(resp).getWriter();
    }

    @Test
    public void doGetExceptionOnWriteTest() throws Exception {
        when(resp.getWriter()).thenReturn(writer);
        doThrow(new RuntimeException("simulated RuntimeException")).when(writer).write(anyString());

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("simulated RuntimeException");

        servlet.doGet(req, resp);
        verify(resp).getWriter();
        verify(writer).write(anyString());
        verify(writer).close();
    }

    @Test
    public void doGetExceptionOnCloseTest() throws Exception {
        when(resp.getWriter()).thenReturn(writer);
        doThrow(new RuntimeException("simulated RuntimeException")).when(writer).close();

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("simulated RuntimeException");

        servlet.doGet(req, resp);
        verify(resp).getWriter();
        verify(writer).write(anyString());
        verify(writer).close();
    }
}
package edu.princeton.library.dspace;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.apache.log4j.Logger;

import org.dspace.core.Context;
import org.dspace.eperson.EPerson;

@PowerMockIgnore({"org.apache.http.conn.ssl.*", "javax.net.ssl.*" , "javax.crypto.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest(EPerson.class)
public class ExampleTaskUnitTest {
  private static final Logger log = Logger.getLogger(ExampleTaskUnitTest.class);

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @Before
  public void setUpMocks() throws Exception {
    mockStatic(EPerson.class);
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  public void testRun() throws Exception {
    EPerson mockEPerson = mock(EPerson.class);
    when(EPerson.findByEmail( any(Context.class), anyString() )).thenReturn(mockEPerson);

    final String[] taskArgs = new String[] {"-euser@localhost.localdomain"};
    ExampleTask.main(taskArgs);

    final String out = outContent.toString();
    assertTrue(out.contains("Successfully found the user user@localhost.localdomain\n"));
  }

  @Test
  public void testRunWithInvalidEmail() throws Exception {
    final String[] taskArgs = new String[] {"-einvalid@localhost.localdomain"};
    ExampleTask.main(taskArgs);

    final String err = errContent.toString();
    assertTrue(err.contains("Failed to find the user invalid@localhost.localdomain - is this a valid e-mail address?"));
  }

  @Test
  public void testRunWithMissingArgs() throws Exception {
    final String[] taskArgs = new String[] {};

    try {
      ExampleTask.main(taskArgs);
      fail("Failed to raise an exception");
    } catch (IllegalArgumentException exception) {
      final String exceptionMessage = exception.getMessage();
    }
  }

}

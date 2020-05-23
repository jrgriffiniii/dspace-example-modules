package edu.princeton.library.dspace;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EPerson.class)
public class ExampleTaskTest {
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
    final Context context = new Context();
    context.turnOffAuthorisationSystem();

/*
    Group.initDefaultGroupNames(context);

    final EPerson eperson = EPerson.create(context);
    eperson.setFirstName("first");
    eperson.setLastName("last");
    eperson.setEmail("test@email.com");
    eperson.setCanLogIn(true);
    eperson.setLanguage(I18nUtil.getDefaultLocale().getLanguage());
    // actually save the eperson to unit testing DB
    eperson.update();

    // Set our global test EPerson as the current user in DSpace
    context.setCurrentUser(eperson);
*/
    when(EPerson.findByEmail( any(Context.class), anyString() )).thenReturn(null);

    context.restoreAuthSystemState();
    context.commit();

    final String[] taskArgs = new String[] {"-euser@localhost.localdomain"};
    ExampleTask.main(taskArgs);
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

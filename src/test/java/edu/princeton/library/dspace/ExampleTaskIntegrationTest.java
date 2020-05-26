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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.dspace.app.util.MockUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.I18nUtil;
import org.dspace.discovery.MockIndexEventConsumer;

import org.dspace.servicemanager.DSpaceKernelImpl;
import org.dspace.servicemanager.DSpaceKernelInit;
import org.dspace.storage.rdbms.MockDatabaseManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;

@PowerMockIgnore({"org.apache.http.conn.ssl.*", "javax.net.ssl.*" , "javax.crypto.*"})
public class ExampleTaskIntegrationTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  private static final Logger log = Logger.getLogger(ExampleTaskIntegrationTest.class);
  protected static Properties testProps;
  protected Context context;
  protected static DSpaceKernelImpl kernelImpl;

  @BeforeClass
  public static void initOnce()
  {
    try
    {
      //set a standard time zone for the tests
      TimeZone.setDefault(TimeZone.getTimeZone("Europe/Dublin"));

      //load the properties of the tests
      testProps = new Properties();
      URL properties = ExampleTaskIntegrationTest.class.getClassLoader()
        .getResource("test-config.properties");
      testProps.load(properties.openStream());

      //load the test configuration file
      ConfigurationManager.loadConfig(null);

      // Initialise the service manager kernel
      kernelImpl = DSpaceKernelInit.getKernel(null);
      if (!kernelImpl.isRunning())
      {
        kernelImpl.start(ConfigurationManager.getProperty("dspace.dir"));
      }

      // Applies/initializes our mock database by invoking its constructor
      // (NOTE: This also initializes the DatabaseManager, which in turn
      // calls DatabaseUtils to initialize the entire DB via Flyway)
      //new MockDatabaseManager();

      // Initialize mock indexer (which does nothing, since Solr isn't running)
      new MockIndexEventConsumer();

      // Initialize mock Util class
      new MockUtil();
    } 
    catch (IOException ex)
    {
      log.error("Error initializing tests", ex);
      fail("Error initializing tests");
    }
  }

  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  /**
   * This is used to delete the DSpace kernel context after each test
   */
  protected void cleanupContext(Context c)
  {
    // If context still valid, abort it
    if(c!=null && c.isValid())
      c.abort();

    // Cleanup Context object by setting it to null
    if(c!=null)
      c = null;
  }

  @After
  public void destroy()
  {
    cleanupContext(context);
  }

  @AfterClass
  public static void destroyOnce()
  {
    //we clear the properties
    testProps.clear();
    testProps = null;

    //Also clear out the kernel & nullify (so JUnit will clean it up)
    if (kernelImpl!=null) {
      kernelImpl.destroy();
    }

    kernelImpl = null;
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  protected void setUpEPerson() throws SQLException, AuthorizeException {
    final Context context = new Context();
    context.turnOffAuthorisationSystem();

    EPerson eperson = null;
    eperson = EPerson.findByEmail(context, "test@email.com");
    if(eperson == null)
    {
      eperson = EPerson.create(context);
      eperson.setFirstName("first");
      eperson.setLastName("last");
      eperson.setEmail("test@email.com");
      eperson.setCanLogIn(true);
      eperson.setLanguage(I18nUtil.getDefaultLocale().getLanguage());
      eperson.update();
    }

    // Set our global test EPerson as the current user in DSpace
    context.setCurrentUser(eperson);
    //Group.initDefaultGroupNames(context);

    context.restoreAuthSystemState();
    context.commit();
  }

  @Test
  public void testRun() throws Exception {
    setUpEPerson();
    final String[] taskArgs = new String[] {"-etest@email.com"};
    ExampleTask.main(taskArgs);

    final String out = outContent.toString();
    assertTrue(out.contains("Successfully found the user test@email.com\n"));
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
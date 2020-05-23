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

@RunWith(PowerMockRunner.class)
@PrepareForTest(EPerson.class)
public class ExampleTaskTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  private static final Logger log = Logger.getLogger(ExampleTaskTest.class);
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
      URL properties = ExampleTaskTest.class.getClassLoader()
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
      new MockDatabaseManager();

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

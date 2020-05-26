package edu.princeton.library.dspace;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import org.dspace.core.Context;
import org.dspace.eperson.EPerson;

public final class ExampleTask {

  public static void main(String[] argv) throws Exception {
    CommandLineParser parser = new PosixParser();
    Options options = new Options();

    options.addOption("e", "email", true, "user e-mail address");

    CommandLine line = parser.parse(options, argv);

    if (line.hasOption("e")) {
      final String email = line.getOptionValue("e");
      final Context context = new Context();
      context.turnOffAuthorisationSystem();

      EPerson eperson = null;
      eperson = EPerson.findByEmail(context, email);
      if(eperson == null)
      {
        System.err.println(
            "Failed to find the user " + email + " - is this a valid e-mail address?");
      } else {
        System.out.println("Successfully found the user " + email);
      }

      context.restoreAuthSystemState();
    } else {
      throw new IllegalArgumentException("Missing email argument");
    }
  }
}

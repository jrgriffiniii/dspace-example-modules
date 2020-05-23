package edu.princeton.library.dspace;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public final class ExampleTask {

  public static void main(String[] argv) throws Exception {
    CommandLineParser parser = new PosixParser();
    Options options = new Options();

    options.addOption("e", "email", true, "user e-mail address");

    CommandLine line = parser.parse(options, argv);

    if (line.hasOption("e")) {
      final String email = line.getOptionValue("e");

      if (email.isEmpty()) {
        System.err.println(
            "Failed to find the user " + email + " - is this a valid e-mail address?");
      } else {
        System.out.println("Successfully found the user " + email);
      }
    } else {
      throw new IllegalArgumentException("Missing email argument");
    }
  }
}

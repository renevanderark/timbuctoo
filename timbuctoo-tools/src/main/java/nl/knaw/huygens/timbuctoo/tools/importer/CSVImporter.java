package nl.knaw.huygens.timbuctoo.tools.importer;

/*
 * #%L
 * Timbuctoo tools
 * =======
 * Copyright (C) 2012 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Base class for handling CSV files.
 */
public abstract class CSVImporter {

  private static final char SEPARATOR_CHAR = ';';
  private static final char QUOTE_CHAR = '"';
  private static final int LINES_TO_SKIP = 0;

  protected final PrintWriter out;
  protected final int linesToSkip;
  protected final char separatorChar;
  protected final char quoteChar;

  public CSVImporter(PrintWriter out, char separator, char quote, int skip) {
    this.out = out;
    separatorChar = separator;
    quoteChar = quote;
    linesToSkip = skip;
  }

  public CSVImporter(PrintWriter out, char separator, char quote) {
    this(out, separator, quote, LINES_TO_SKIP);
  }

  public CSVImporter(PrintWriter out) {
    this(out, SEPARATOR_CHAR, QUOTE_CHAR, LINES_TO_SKIP);
  }

  public CSVImporter() {
    this(null, SEPARATOR_CHAR, QUOTE_CHAR, LINES_TO_SKIP);
  }

  public void handleFile(File file, int itemsPerLine, boolean verbose) throws Exception {
    if (!file.canRead()) {
      throw new FileNotFoundException("Missing CSV file " + file.getName());
    }
    handleFile(new FileInputStream(file), itemsPerLine, verbose);
  }

  public void handleFile(String filename, int itemsPerLine, boolean verbose) throws Exception {
    handleFile(new FileInputStream(filename), itemsPerLine, verbose);
  }

  public void handleFile(InputStream stream, int itemsPerLine, boolean verbose) throws Exception {
    initialize();
    CSVReader reader = null;
    try {
      Reader fileReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
      reader = new CSVReader(fileReader, separatorChar, quoteChar, linesToSkip);
      for (String[] line : reader.readAll()) {
        if (acceptLine(line)) {
          validateLine(line, itemsPerLine, verbose);
          handleLine(line);
        }
      }
      handleEndOfFile();
    } finally {
      if (reader != null) {
        reader.close();
      }
      if (out != null) {
        out.flush();
      }
    }
  }

  /**
   * Performs initialization before handling of a file.
   */
  protected void initialize() throws Exception {}

  /**
   * Performa actions after file has been handled.
   */
  protected void handleEndOfFile() throws Exception {};

  /**
   * Return {@code true} if the line must be handled, {@code false} otherwise.
   */
  protected boolean acceptLine(String[] items) {
    // Note: CSVReader converts an empty line to a single, empty item...
    return (items.length != 0) && !items[0].isEmpty() && !isComment(items);
  }

  private boolean isComment(String[] items) {
    return items[0].startsWith("--");
  }

  /**
   * Handles a parsed input line.
   */
  protected abstract void handleLine(String[] items) throws Exception;

  private void validateLine(String[] line, int itemsPerLine, boolean verbose) {
    boolean error = (line.length < itemsPerLine);
    if (error || verbose) {
      out.println();
      for (String word : line) {
        out.println("[" + word + "]");
      }
      if (error) {
        out.println("## Number of items < " + itemsPerLine);
        out.flush();
        throw new RuntimeException("Error on line '" + line[0] + "...'");
      }
    }
  }

  protected void displayError(String message, String[] line) {
    out.printf("%n## %s%n", message);
    out.printf("   [%s]%n", StringUtils.join(line, "] ["));
    out.flush();
  }

}

package edu.berkeley.nlp.assignments.parsing.io; 
import edu.berkeley.nlp.assignments.parsing.util.logging.Redwood;

import edu.berkeley.nlp.assignments.parsing.util.ByteStreamGobbler;
import edu.berkeley.nlp.assignments.parsing.util.RuntimeInterruptedException;
import edu.berkeley.nlp.assignments.parsing.util.StreamGobbler;

import java.io.*;

/**
* Opens a outputstream for writing into a bzip2 file by piping into the bzip2 command.
* Output from bzip2 command is written into the specified file.
* 
* @author Angel Chang
*/
public class BZip2PipedOutputStream extends OutputStream
{
  private String filename;
  private Process process;
  private ByteStreamGobbler outGobbler;
  private StreamGobbler errGobbler;
  private PrintWriter errWriter;

  public BZip2PipedOutputStream(String filename) throws IOException {
    this(filename, System.err);
  }

  public BZip2PipedOutputStream(String filename, OutputStream err) throws IOException {
    String bzip2 = System.getProperty("bzip2", "bzip2");
    String cmd = bzip2; // + " > " + filename;
    //log.info("getBZip2PipedOutputStream: Running command: "+cmd);
    ProcessBuilder pb = new ProcessBuilder();
    pb.command(cmd);
    this.process = pb.start();
    this.filename = filename;
    OutputStream outStream = new FileOutputStream(filename);
    errWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(err)));
    outGobbler = new ByteStreamGobbler("Output stream gobbler: " + cmd + " " + filename,
            process.getInputStream(), outStream);
    errGobbler = new StreamGobbler(process.getErrorStream(), errWriter);
    outGobbler.start();
    errGobbler.start();
  }

  public void flush() throws IOException
  {
    process.getOutputStream().flush();
  }

  public void write(int b) throws IOException
  {
    process.getOutputStream().write(b);
  }

  public void close() throws IOException
  {
    process.getOutputStream().close();
    try {
      outGobbler.join();
      errGobbler.join();
      outGobbler.getOutputStream().close();
      process.waitFor();
    } catch (InterruptedException ex) {
      throw new RuntimeInterruptedException(ex);
    }
    //log.info("getBZip2PipedOutputStream: Closed. ");
  }
}

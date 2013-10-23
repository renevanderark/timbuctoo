package nl.knaw.huygens.timbuctoo.messages;

import javax.jms.JMSException;

public interface Consumer {

  public abstract Action receive() throws JMSException;

  public abstract void close() throws JMSException;

  public abstract void closeQuietly();

}
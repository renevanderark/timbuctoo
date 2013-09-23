package nl.knaw.huygens.repository.messages;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQProducer implements Producer {

  private static final Logger LOG = LoggerFactory.getLogger(ActiveMQProducer.class);

  private final String name;
  private Connection connection;
  private Session session;
  private MessageProducer producer;

  public ActiveMQProducer(ConnectionFactory factory, String queue, String name) throws JMSException {
    this.name = name;
    LOG.info("Creating '{}'", name);
    connection = factory.createConnection();
    connection.start();
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Destination destination = session.createQueue(queue);
    producer = session.createProducer(destination);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    LOG.info("Created '{}'", name);
  }

  @Override
  public void send(ActionType action, String type, String id) throws JMSException {
    Message message = session.createMessage();
    message.setStringProperty(Broker.PROP_ACTION, action.getStringRepresentation());
    message.setStringProperty(Broker.PROP_DOC_TYPE, type);
    message.setStringProperty(Broker.PROP_DOC_ID, id);
    producer.send(message);
  }

  @Override
  public void close() throws JMSException {
    LOG.info("Closing '{}'", name);
    session.close();
    connection.close();
  }

  @Override
  public void closeQuietly() {
    try {
      close();
    } catch (JMSException e) {
      LOG.error("Error while closing", e);
    }
  }

}

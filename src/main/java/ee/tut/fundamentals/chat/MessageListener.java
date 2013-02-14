package ee.tut.fundamentals.chat;

public interface MessageListener {

  /**
   * Triggered when an IM is received from the server
   */
  public void onMessage(String msg);
}

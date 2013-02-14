package ee.tut.fundamentals.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;


/**
 * Simple JavaFX based chat client
 * By default it uses the ports 8888 and 8080 to communicate with the chat server.
 * This class sets up ChatClient class which handles all of the communication,
 * as well as the JavaFX ui which is defined by Chat.fxml and ChatController class
 */
public class ChatClientFx extends Application {

  public static void main(String[] args) {
    launch(args);
  }



  @Override
  public void start(Stage stage) throws Exception {

    //set up JavaFX GUI
    URL url = getClass().getResource("Chat.fxml");
    if (url == null) {
      System.out.println("Unable to read fxml");
      System.exit(-1);
    }
    System.out.println("url: " + url);
    FXMLLoader loader = new FXMLLoader(url);
    BorderPane root = (BorderPane) loader.load();

    stage.setScene(new Scene(root));
    stage.setTitle("Fundamental Chat");


    //get the controller, we need to configure it further
    ChatController controller = loader.getController();


    //set up ChatClient
    Map<String, String> params = getParameters().getNamed();
    if (!params.containsKey("name") || !params.containsKey("host")) {
      usage();
    }
    String user = params.get("name");
    String host = params.get("host");

    int serverPort = 8888;
    int httpPort=8080;

    if (params.containsKey("serverPort")) {
      serverPort = Integer.valueOf(params.get("serverPort"));
    }
    if (params.containsKey("httpPort")) {
      httpPort = Integer.valueOf(params.get("httpPort"));
    }

    try {
      ChatClient client = new ChatClient(user, host, serverPort, httpPort);

      //Configure the controller to use the ChatClient
      controller.setChatClient(client);
      client.setMessageListener(controller);
    }
    catch (Exception e) {
      stage.setScene(buildErrorScene());
    }

    //show the GUI
    stage.show();
  }

  public Scene buildErrorScene() throws IOException {
    URL url = getClass().getClassLoader().getResource("Err.fxml");
    if (url == null) {
      System.out.println("Unable to read fxml");
      System.exit(-1);
    }
    Parent err = FXMLLoader.load(url);
    return new Scene(err);
  }

  public void usage() {
    System.out.println("Usage: java -jar ChatClientFx.jar --name=yourName --host=hostname [--serverPort=number] [--httpPort=number]");
    System.exit(-1);
  }

}

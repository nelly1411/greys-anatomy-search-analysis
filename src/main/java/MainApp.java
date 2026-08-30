import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox();
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
                "Frage 1: Schauspieler-Paare",
                "Frage 2: Begriffe in Verbindung zu Grey's Anatomy",
                "Frage 3: Suchanfragen zum Soundtrack",
                "Frage 4: Häufig besuchte Webseiten",
                "Frage 5: Suchanfragen zum Staffelfinale",
                "Frage 6: Häufigsten Anfragen zu Grey's Anatomy",
                "Frage 7: Meistgesuchter Schauspieler",
                "Frage 8: Zeitraum mit häufigsten Anfragen",
                "Frage 9: Charakter-Paare",
                "Frage 10: Höchsten Aufrufe im Monat"
        );

        vbox.getChildren().add(comboBox);

        comboBox.setOnAction(event -> {
            vbox.getChildren().removeIf(node -> !(node instanceof ComboBox));
            switch (comboBox.getValue()) {
                case "Frage 1: Schauspieler-Paare" -> vbox.getChildren().add(new Frage1().createBarChart());
                case "Frage 2: Begriffe in Verbindung zu Grey's Anatomy" -> vbox.getChildren().add(new Frage2().createBarChart2());
                case "Frage 3: Suchanfragen zum Soundtrack" -> vbox.getChildren().add(new Frage3().createSoundtrackBarChart());
                case "Frage 4: Häufig besuchte Webseiten" -> vbox.getChildren().add(new Frage4().createClickUrlBarChart());
                case "Frage 5: Suchanfragen zum Staffelfinale" -> vbox.getChildren().add(new Frage5().createFinaleBarChart());
                case "Frage 6: Häufigsten Anfragen zu Grey's Anatomy" -> vbox.getChildren().add(new Frage6().createPieChartForTopQueries());
                case "Frage 7: Meistgesuchter Schauspieler" -> vbox.getChildren().add(new Frage7().createBarChartForActorPairs());
                case "Frage 8: Zeitraum mit häufigsten Anfragen" -> vbox.getChildren().add(new Frage8().createLineChartForHourlyQueries());
                case "Frage 9: Charakter-Paare" -> vbox.getChildren().add(new Frage9().createBarChartForCharacterPairs());
                case "Frage 10: Höchsten Aufrufe im Monat" -> vbox.getChildren().add(new Frage10().createPieChartForMonthlyQueries());
            }
        });

        Scene scene = new Scene(vbox, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Visualisierung von Suchanfragen");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

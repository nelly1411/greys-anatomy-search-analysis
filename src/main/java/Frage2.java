import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Frage2 {

    public VBox createBarChart2() {
        // Achsen erstellen
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Wörter");
        yAxis.setLabel("Häufigkeit");

        // BarChart erstellen
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Häufigkeit der Wörter in Suchanfragen zu Grey's Anatomy");

        XYChart.Series<String, Number> series = new XYChart.Series<>();



        List<String> legendEntries = new ArrayList<>();
        String[] farben = {"#3498db", "#e74c3c", "#2ecc71", "#9b59b6", "#f39c12"};
        int index = 0;
      
        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
              System.out.println("Verbindung erfolgreich!");

            String sql = """
                WITH Words AS (
                    SELECT unnest(string_to_array(LOWER("Query"), ' ')) AS Word
                    FROM search_data
                    WHERE LOWER("Query") LIKE '%greys anatomy%'
                )
                SELECT Word, COUNT(*) AS Word_Count
                FROM Words
                GROUP BY Word
                ORDER BY Word_Count DESC
                LIMIT 5;
            """;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String word = resultSet.getString("Word");
                int count = resultSet.getInt("Word_Count");
                XYChart.Data<String, Number> data = new XYChart.Data<>(word, count);
                series.getData().add(data);

                // Farbe für jeden Balken setzen
                String style = "-fx-bar-fill: " + farben[index % farben.length] + ";";
                data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        newValue.setStyle(style);
                    }
                });

                // Legenden-Eintrag speichern
                legendEntries.add(String.format("%s: %d Treffer", word, count));
                index++;
            }

        } catch (Exception e) {
            System.out.println("Verbindung fehlgeschlagen: " + e.getMessage());
        }

        barChart.getData().add(series);
        barChart.setLegendVisible(false);  // Standard-Legende ausblenden

        // Benutzerdefinierte Legende hinzufügen
        HBox legendBox = new HBox(10);
        for (int i = 0; i < legendEntries.size(); i++) {
            Label legendLabel = new Label(legendEntries.get(i));
            legendLabel.setStyle("-fx-background-color: " + farben[i % farben.length] + "; -fx-padding: 5px; -fx-text-fill: white; -fx-border-color: black; -fx-border-radius: 5px;");
            legendBox.getChildren().add(legendLabel);
        }

        return new VBox(barChart, legendBox);
    }
}

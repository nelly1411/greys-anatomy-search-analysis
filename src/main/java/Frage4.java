import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Frage4 {

    public VBox createClickUrlBarChart() {
        // Achsen definieren
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("ClickURL");
        yAxis.setLabel("Aufrufanzahl");

        // BarChart erstellen
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Aufrufhäufigkeit der Click-URLs zu Grey's Anatomy");

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<String> legendEntries = new ArrayList<>();
        String[] farben = {"#3498db", "#e74c3c", "#2ecc71", "#9b59b6", "#f39c12"};  // Verschiedene Farben
        int index = 0;
        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");

            String sql = """
                    SELECT "ClickURL", COUNT(*) AS Aufruf_Count
                    FROM search_data
                    WHERE LOWER("Query") LIKE '%greys anatomy%'
                    AND "ClickURL" IS NOT NULL
                    GROUP BY "ClickURL"
                    ORDER BY Aufruf_Count DESC
                    LIMIT 5;
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String clickUrl = resultSet.getString("ClickURL");
                int aufrufCount = resultSet.getInt("Aufruf_Count");
                XYChart.Data<String, Number> data = new XYChart.Data<>(clickUrl, aufrufCount);
                series.getData().add(data);

                // Farbe für jeden Balken setzen
                String style = "-fx-bar-fill: " + farben[index % farben.length] + ";";
                data.nodeProperty().addListener((observable, oldValue, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(style);
                    }
                });

                // Legenden-Eintrag speichern
                legendEntries.add(String.format("%s: %d Treffer", clickUrl, aufrufCount));
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
            legendLabel.setStyle("-fx-background-color: " + farben[i % farben.length] + "; -fx-padding: 5px; -fx-text-fill: white;");
            legendBox.getChildren().add(legendLabel);
        }

        return new VBox(barChart, legendBox);
    }
}

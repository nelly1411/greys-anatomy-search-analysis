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

public class Frage9 {

    public VBox createBarChartForCharacterPairs() {
        // Achsen definieren
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Charakter-Paar");
        yAxis.setLabel("Häufigkeit der Suchanfragen");

        // BarChart erstellen
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Suchhäufigkeit der Charakter-Paare in Grey's Anatomy");

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<String> legendEntries = new ArrayList<>();
        String[] farben = {"#3498db", "#e74c3c", "#2ecc71", "#9b59b6", "#f39c12", "#8e44ad"};
        int index = 0;
      
        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");

            String sql = """
                SELECT
                    CASE
                        WHEN ("Query" LIKE '%meredith%' AND "Query" LIKE '%derek%') THEN 'Meredith & Derek'
                        WHEN ("Query" LIKE '%cristina%' AND "Query" LIKE '%preston%') THEN 'Cristina & Preston'
                        WHEN ("Query" LIKE '%alex%' AND "Query" LIKE '%izzie%') THEN 'Alex & Izzie'
                        WHEN ("Query" LIKE '%george%' AND "Query" LIKE '%callie%') THEN 'George & Callie'
                        WHEN ("Query" LIKE '%meredith%' AND "Query" LIKE '%finn%') THEN 'Meredith & Finn'
                        WHEN ("Query" LIKE '%izzie%' AND "Query" LIKE '%denny%') THEN 'Izzie & Denny'
                    END AS Character_Pair,
                    COUNT(*) AS Pair_Frequency
                FROM public.search_data
                WHERE
                    ("Query" LIKE '%meredith%' AND "Query" LIKE '%derek%')
                    OR ("Query" LIKE '%cristina%' AND "Query" LIKE '%preston%')
                    OR ("Query" LIKE '%alex%' AND "Query" LIKE '%izzie%')
                    OR ("Query" LIKE '%george%' AND "Query" LIKE '%callie%')
                    OR ("Query" LIKE '%meredith%' AND "Query" LIKE '%finn%')
                    OR ("Query" LIKE '%izzie%' AND "Query" LIKE '%denny%')
                GROUP BY Character_Pair
                ORDER BY Pair_Frequency DESC;
            """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String pair = resultSet.getString("Character_Pair");
                int frequency = resultSet.getInt("Pair_Frequency");
                XYChart.Data<String, Number> data = new XYChart.Data<>(pair, frequency);
                series.getData().add(data);

                // Farbe für jeden Balken setzen
                String style = "-fx-bar-fill: " + farben[index % farben.length] + ";";
                data.nodeProperty().addListener((observable, oldValue, newValue) -> newValue.setStyle(style));

                // Legenden-Eintrag speichern
                legendEntries.add(String.format("%s: %d Treffer", pair, frequency));
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

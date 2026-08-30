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

public class Frage7 {

    public VBox createBarChartForActorPairs() {
        // Achsen definieren
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Schauspieler-Paar");
        yAxis.setLabel("Häufigkeit der Suchanfragen");

        // BarChart erstellen
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Suchhäufigkeit der Schauspieler-Paare in Grey's Anatomy");

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<String> legendEntries = new ArrayList<>();
        String[] farben = {"#3498db", "#e74c3c", "#2ecc71", "#9b59b6", "#f39c12", "#8e44ad", "#1abc9c", "#d35400"};
        int index = 0;
      
        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");

          
            String sql = """
                SELECT
                    CASE
                        WHEN LOWER("Query") LIKE '%meredith grey%' OR LOWER("Query") LIKE '%ellen pompeo%' THEN 'Meredith / Ellen'
                        WHEN LOWER("Query") LIKE '%cristina yang%' OR LOWER("Query") LIKE '%sandra oh%' THEN 'Cristina / Sandra'
                        WHEN LOWER("Query") LIKE '%alex karev%' OR LOWER("Query") LIKE '%justin chambers%' THEN 'Alex / Justin'
                        WHEN LOWER("Query") LIKE '%izzie stevens%' OR LOWER("Query") LIKE '%katherine heigl%' THEN 'Izzie / Katherine'
                        WHEN LOWER("Query") LIKE '%george o malley%' OR LOWER("Query") LIKE '%t. r. knight%' THEN 'George / T. R.'
                        WHEN LOWER("Query") LIKE '%derek shepherd%' OR LOWER("Query") LIKE '%patrick dempsey%' THEN 'Derek / Patrick'
                        WHEN LOWER("Query") LIKE '%preston burke%' OR LOWER("Query") LIKE '%isaiah washington%' THEN 'Preston / Isaiah'
                        WHEN LOWER("Query") LIKE '%richard webber%' OR LOWER("Query") LIKE '%james pickens jr.%' THEN 'Richard / James'
                        WHEN LOWER("Query") LIKE '%addison montgomery%' OR LOWER("Query") LIKE '%kate walsh%' THEN 'Addison / Kate'
                        ELSE 'Andere'
                    END AS Kurzname,
                    COUNT(*) AS Häufigkeit
                FROM public.search_data
                WHERE
                    LOWER("Query") LIKE '%meredith grey%' OR LOWER("Query") LIKE '%ellen pompeo%'
                    OR LOWER("Query") LIKE '%cristina yang%' OR LOWER("Query") LIKE '%sandra oh%'
                    OR LOWER("Query") LIKE '%alex karev%' OR LOWER("Query") LIKE '%justin chambers%'
                    OR LOWER("Query") LIKE '%izzie stevens%' OR LOWER("Query") LIKE '%katherine heigl%'
                    OR LOWER("Query") LIKE '%george o malley%' OR LOWER("Query") LIKE '%t. r. knight%'
                    OR LOWER("Query") LIKE '%derek shepherd%' OR LOWER("Query") LIKE '%patrick dempsey%'
                    OR LOWER("Query") LIKE '%preston burke%' OR LOWER("Query") LIKE '%isaiah washington%'
                    OR LOWER("Query") LIKE '%richard webber%' OR LOWER("Query") LIKE '%james pickens jr.%'
                    OR LOWER("Query") LIKE '%addison montgomery%' OR LOWER("Query") LIKE '%kate walsh%'
                GROUP BY Kurzname
                ORDER BY Häufigkeit DESC;
            """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("Kurzname");
                int count = resultSet.getInt("Häufigkeit");
                XYChart.Data<String, Number> data = new XYChart.Data<>(name, count);
                series.getData().add(data);

                // Farbe für jeden Balken setzen
                String style = "-fx-bar-fill: " + farben[index % farben.length] + ";";
                data.nodeProperty().addListener((observable, oldValue, newValue) -> newValue.setStyle(style));

                // Legenden-Eintrag speichern
                legendEntries.add(String.format("%s: %d Treffer", name, count));
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

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

public class Frage1 {

    public VBox createBarChart() {
        // Achsen definieren
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Schauspieler-Paar");
        yAxis.setLabel("Anzahl der Suchanfragen");

        // Balkendiagramm erstellen
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Häufigste Anfragen zu Schauspieler-Paaren in Grey's Anatomy");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Suchanfragen");

        List<String> legendEntries = new ArrayList<>();
        String[] colors = {"#3498db", "#e74c3c", "#2ecc71", "#9b59b6", "#f39c12", "#1abc9c", "#8e44ad", "#34495e"};
        int colorIndex = 0;
      
        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");

            String sql = """
                WITH SchauspielerGruppiert AS (
                    SELECT 
                        "Query" AS Suchanfrage,
                        CASE
                            WHEN LOWER("Query") LIKE '%meredith grey%' OR LOWER("Query") LIKE '%ellen pompeo%' THEN 'Meredith Grey / Ellen Pompeo'
                            WHEN LOWER("Query") LIKE '%derek shepherd%' OR LOWER("Query") LIKE '%patrick dempsey%' THEN 'Derek Shepherd / Patrick Dempsey'
                            WHEN LOWER("Query") LIKE '%izzie stevens%' OR LOWER("Query") LIKE '%katherine heigl%' THEN 'Izzie Stevens / Katherine Heigl'
                            WHEN LOWER("Query") LIKE '%george o''malley%' OR LOWER("Query") LIKE '%t.r. knight%' THEN 'George O''Malley / T.R. Knight'
                            WHEN LOWER("Query") LIKE '%cristina yang%' OR LOWER("Query") LIKE '%sandra oh%' THEN 'Cristina Yang / Sandra Oh'
                            WHEN LOWER("Query") LIKE '%alex karev%' OR LOWER("Query") LIKE '%justin chambers%' THEN 'Alex Karev / Justin Chambers'
                            WHEN LOWER("Query") LIKE '%dr. bailey%' OR LOWER("Query") LIKE '%chandra wilson%' THEN 'Dr. Bailey / Chandra Wilson'
                            WHEN LOWER("Query") LIKE '%chief webber%' OR LOWER("Query") LIKE '%james pickens jr.%' THEN 'Chief Webber / James Pickens Jr.'
                            ELSE 'Other'
                        END AS Actor_Character
                    FROM public.search_data
                    WHERE 
                        LOWER("Query") LIKE '%meredith grey%' OR LOWER("Query") LIKE '%ellen pompeo%' OR
                        LOWER("Query") LIKE '%derek shepherd%' OR LOWER("Query") LIKE '%patrick dempsey%' OR
                        LOWER("Query") LIKE '%izzie stevens%' OR LOWER("Query") LIKE '%katherine heigl%' OR
                        LOWER("Query") LIKE '%george o''malley%' OR LOWER("Query") LIKE '%t.r. knight%' OR
                        LOWER("Query") LIKE '%cristina yang%' OR LOWER("Query") LIKE '%sandra oh%' OR
                        LOWER("Query") LIKE '%alex karev%' OR LOWER("Query") LIKE '%justin chambers%' OR
                        LOWER("Query") LIKE '%dr. bailey%' OR LOWER("Query") LIKE '%chandra wilson%' OR
                        LOWER("Query") LIKE '%chief webber%' OR LOWER("Query") LIKE '%james pickens jr.%'
                )
                SELECT 
                    Actor_Character,
                    COUNT(*) AS Suchanzahl,
                    MAX(Suchanfrage) AS Meistgesuchte_Anfrage
                FROM SchauspielerGruppiert
                GROUP BY Actor_Character
                ORDER BY Suchanzahl DESC;
            """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String actorCharacter = resultSet.getString("Actor_Character");
                int count = resultSet.getInt("Suchanzahl");
                XYChart.Data<String, Number> data = new XYChart.Data<>(actorCharacter, count);
                series.getData().add(data);

                // Kürzere Legenden-Einträge (erster Teil des Namens + Anzahl der Treffer)
                String shortEntry = actorCharacter.split(" / ")[0] + ": " + count + " Treffer";
                legendEntries.add(shortEntry);

                // Balkenfarbe setzen
                String colorStyle = "-fx-bar-fill: " + colors[colorIndex % colors.length] + ";";
                data.nodeProperty().addListener((observable, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(colorStyle);
                    }
                });
                colorIndex++;
            }

        } catch (Exception e) {
            System.out.println("Verbindung fehlgeschlagen: " + e.getMessage());
        }

        barChart.getData().add(series);
        barChart.setLegendVisible(false); // Standard-Legende ausblenden

        // Benutzerdefinierte Legende hinzufügen
        HBox legendBox = new HBox(10);
        for (int i = 0; i < legendEntries.size(); i++) {
            Label legendLabel = new Label(legendEntries.get(i));
            legendLabel.setStyle("-fx-background-color: " + colors[i % colors.length] + "; -fx-padding: 5px; -fx-text-fill: white; -fx-border-radius: 5px; -fx-border-color: black;");
            legendBox.getChildren().add(legendLabel);
        }

        return new VBox(barChart, legendBox);
    }
}

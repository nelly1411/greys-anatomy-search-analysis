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

public class Frage5 {

    public VBox createFinaleBarChart() {
        // Achsen definieren
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Suchbegriff");
        yAxis.setLabel("Anzahl der Suchanfragen");

        // BarChart erstellen
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Suchanfragen zu Grey's Anatomy Finale");

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        int count = 0;  // Variable zur Speicherung der Trefferanzahl

      
        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");
          
            String sql = """
                SELECT COUNT(*) AS TotalSearchCount
                FROM public.search_data
                WHERE LOWER("Query") LIKE '%greys anatomy finale%'
                OR LOWER("Query") LIKE '%greys anatomy season finale%';
            """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt("TotalSearchCount");

                // Daten hinzufügen
                series.getData().add(new XYChart.Data<>("Grey's Anatomy Finale", count));
            }

        } catch (Exception e) {
            System.out.println("Verbindung fehlgeschlagen: " + e.getMessage());
        }

        // Daten dem Diagramm hinzufügen
        barChart.getData().add(series);
        barChart.setLegendVisible(false);  // Standardlegende ausblenden

        // Benutzerdefinierte Legende hinzufügen
        HBox legendBox = new HBox();
        Label legendLabel = new Label("Grey's Anatomy Finale: " + count + " Treffer");
        legendLabel.setStyle("-fx-background-color: #e74c3c; -fx-padding: 5px; -fx-text-fill: white;");
        legendBox.getChildren().add(legendLabel);

        return new VBox(barChart, legendBox);
    }
}

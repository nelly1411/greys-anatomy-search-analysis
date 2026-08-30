import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Frage3 {

    public VBox createSoundtrackBarChart() {
        // Achsen definieren
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Suchbegriff");
        yAxis.setLabel("Anzahl der Suchanfragen");

        // Erstellen des Balkendiagramms
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Suchanfragen zu Grey's Anatomy Soundtrack");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
      
        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");

            String sql = "SELECT COUNT(*) AS soundtrack_searches FROM search_data WHERE LOWER(\"Query\") LIKE '%greys anatomy soundtrack%'";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("soundtrack_searches");
                // Legendentext mit dynamischer Trefferanzahl
                series.setName("Soundtrack-Suchanfragen: " + count + " Treffer");
                series.getData().add(new XYChart.Data<>("Grey's Anatomy Soundtrack", count));
            }
        } catch (Exception e) {
            System.out.println("Verbindung fehlgeschlagen: " + e.getMessage());
        }

        // Balkendiagramm-Daten hinzufügen
        barChart.getData().add(series);

        // Rückgabe als VBox, um im GUI integriert zu werden
        return new VBox(barChart);
    }
}

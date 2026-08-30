import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Frage6 {

    public VBox createPieChartForTopQueries() {
        // Tortendiagramm erstellen
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Häufigste Suchanfragen zu Grey's Anatomy");

        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");
          
            String sql = """
                SELECT rq."Query", COUNT(*) AS AnfrageAnzahl
                FROM (
                    SELECT "Query"
                    FROM Relevant_Queries_3
                ) AS rq
                GROUP BY rq."Query"
                ORDER BY AnfrageAnzahl DESC
                LIMIT 10;
            """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Daten dem Tortendiagramm hinzufügen
            while (resultSet.next()) {
                String query = resultSet.getString("Query");
                int count = resultSet.getInt("AnfrageAnzahl");
                PieChart.Data slice = new PieChart.Data(query + " (" + count + ")", count);
                pieChart.getData().add(slice);
            }

        } catch (Exception e) {
            System.out.println("Verbindung fehlgeschlagen: " + e.getMessage());
        }

        return new VBox(pieChart);
    }
}

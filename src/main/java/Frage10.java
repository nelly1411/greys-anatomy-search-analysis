import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Frage10 {

    public VBox createPieChartForMonthlyQueries() {
        // Tortendiagramm erstellen
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Vergleich der Suchanfragen pro Monat zu Grey's Anatomy");


        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");

            String sql = """
                SELECT to_char("QueryTime", 'MM.YY') AS month,
                       COUNT(*) AS anzahl_suchanfrage
                FROM search_data
                WHERE "Query" ILIKE '%Greys Anatomy%'
                GROUP BY to_char("QueryTime", 'MM.YY')
                ORDER BY anzahl_suchanfrage DESC;
            """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String month = resultSet.getString("month");
                int count = resultSet.getInt("anzahl_suchanfrage");

                // Daten zum Tortendiagramm hinzufügen
                PieChart.Data data = new PieChart.Data(month + ": " + count + " Treffer", count);
                pieChart.getData().add(data);
            }

        } catch (Exception e) {
            System.out.println("Verbindung fehlgeschlagen: " + e.getMessage());
        }

        return new VBox(pieChart);
    }
}

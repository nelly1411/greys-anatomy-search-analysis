import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Frage8 {

    public VBox createLineChartForHourlyQueries() {
        // Achsen erstellen
        NumberAxis xAxis = new NumberAxis(0, 23, 1);
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Stunde");
        yAxis.setLabel("Anzahl der Suchanfragen");

        // LineChart erstellen
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Suchanfragen pro Stunde zu Grey's Anatomy");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Suchanfragen pro Stunde");
      
        // Verbindung zur PostgreSQL-Datenbank
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Verbindung erfolgreich!");

            String sql = """
                SELECT EXTRACT(HOUR FROM "QueryTime") AS Stunde, COUNT("ID") AS Anzahl
                FROM public.search_data
                WHERE "Query" ILIKE '%greys anatomy%'
                GROUP BY Stunde
                ORDER BY Stunde;
            """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int stunde = resultSet.getInt("Stunde");
                int anzahl = resultSet.getInt("Anzahl");
                series.getData().add(new XYChart.Data<>(stunde, anzahl));
            }

        } catch (Exception e) {
            System.out.println("Verbindung fehlgeschlagen: " + e.getMessage());
        }

        lineChart.getData().add(series);

        return new VBox(lineChart);
    }
}

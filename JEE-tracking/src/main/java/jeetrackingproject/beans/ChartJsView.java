package jeetrackingproject.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import jeetrackingproject.entities.Planning;
import jeetrackingproject.utils.UtilsConnexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class ChartJsView {
    private LineChartModel lineModel;

    @PostConstruct
    public void init() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        String activite_idS = request.getParameter("activite_id");
        Long activite_id = Long.parseLong(activite_idS);
        getAllPlannigByActivite(activite_id);
    }


    public void getAllPlannigByActivite(Long activite_id) {
        ResultSet rs = null;
        ResultSet rs2 = null;
        lineModel = new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        values.add(0);
        List<String> labels = new ArrayList<>();
        try {
            Connection con = UtilsConnexion.seConnecter();
            String sql = "SELECT * FROM planning WHERE activite_id = ?";
            String sql2 = "SELECT nom FROM activite WHERE id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            PreparedStatement preparedStatement2 = con.prepareStatement(sql2);
            preparedStatement.setLong(1, activite_id);
            preparedStatement2.setLong(1, activite_id);
            rs = preparedStatement.executeQuery();
            rs2 = preparedStatement2.executeQuery();
            while (rs2.next()) {
                String nom = rs2.getString("nom");
                dataSet.setLabel(nom);
            }
            while (rs.next()) {
                Long distance = rs.getLong("distance");
                String date = rs.getString("date");
                Long duree = rs.getLong("duree");
                values.add(duree);
                labels.add(date);
            }
            preparedStatement.close();
            con.close();
            dataSet.setData(values);
            dataSet.setFill(false);
            dataSet.setBorderColor("rgb(75, 192, 192)");
            dataSet.setTension(0.1);
            data.addChartDataSet(dataSet);

            data.setLabels(labels);
            LineChartOptions options = new LineChartOptions();
            Title title = new Title();
            title.setDisplay(true);
            title.setText("Statistiques");
            options.setTitle(title);

            lineModel.setOptions(options);
            lineModel.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }
}

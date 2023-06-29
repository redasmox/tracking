package jeetrackingproject.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.*;

import java.io.Serializable;

@Named
@ViewScoped
public class PolylinesView implements Serializable {

    private MapModel<Long> polylineModel;

    @PostConstruct
    public void init() {
        polylineModel = new DefaultMapModel<>();

        //Shared coordinates
        LatLng coord1 = new LatLng(36.879703, 30.706707);
        LatLng coord2 = new LatLng(36.885260, 30.702323);
        LatLng coord3 = new LatLng(36.879703, 30.706707);
        LatLng coord4 = new LatLng(37.885260, 30.702323);

        //Polyline
        Polyline<Long> polyline = new Polyline<>();
        polyline.setData(1L);
        polyline.getPaths().add(coord1);
        polyline.getPaths().add(coord2);

        polyline.setStrokeWeight(10);
        polyline.setStrokeColor("#FF9900");
        polyline.setStrokeOpacity(0.7);

        polylineModel.addOverlay(polyline);
    }

    public MapModel<Long> getPolylineModel() {
        return polylineModel;
    }

    public void onPolylineSelect(OverlaySelectEvent<Long> event) {
        Overlay<Long> overlay = event.getOverlay();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Polyline " + overlay.getData() + " Selected", null));
    }
}

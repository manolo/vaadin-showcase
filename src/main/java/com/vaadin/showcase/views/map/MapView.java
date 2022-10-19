package com.vaadin.showcase.views.map;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataGenerator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.showcase.views.MainLayout;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@PermitAll
public class MapView extends VerticalLayout {

    private Map map = new Map();
    private MarkerFeature current;

    public MapView() {
        setSizeFull();
        setPadding(false);
        map.getElement().setAttribute("theme", "borderless");
        View view = map.getView();
        view.setCenter(new Coordinate(10.0, 55.0));
        view.setZoom(13);
        addAndExpand(map);

        getElement().executeJs(""
                + "const elm = this;"
                + "navigator.geolocation.watchPosition("
                + "  pos => elm.$server.updateLocation(pos.coords.longitude, pos.coords.latitude),"
                + "  err => {}, "
                + "  { enableHighAccuracy: true, timeout: 5000, maximumAge: 1000 }"
                + ");"
                + "", map.getElement());
    }

    @ClientCallable
    private void updateLocation(double lon, double lat) {
        Coordinate coordinate = new Coordinate(lon, lat);
        if (current == null) {
            current = new MarkerFeature(coordinate, MarkerFeature.PIN_ICON);
            map.getFeatureLayer().addFeature(current);
        }
        current.setCoordinates(coordinate);
        map.getView().setCenter(coordinate);
    }
}

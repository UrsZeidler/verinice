/*******************************************************************************
 * Copyright (c) 2016 Benjamin Weißenfels.
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     @author Benjamin Weißenfels <bw[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.web.poseidon.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.PieChartModel;

import sernet.hui.common.VeriniceContext;
import sernet.verinice.model.bsi.MassnahmenUmsetzung;
import sernet.verinice.service.model.IObjectModelService;
import sernet.verinice.web.Messages;
import sernet.verinice.web.poseidon.services.ControlService;

/**
 *
 * @author Benjamin Weißenfels <bw[at]sernet[dot]de>
 *
 */
@ManagedBean(name = "veriniceChartView")
public class VeriniceCharts implements Serializable {

    private static final String IMPLEMENTATION_STATUS_UNEDITET = "SingleSelectDummyValue";

    private static final long serialVersionUID = 1L;

    @ManagedProperty("#{controlService}")
    private ControlService controlService;

    private PieChartModel pieModel;

    private HorizontalBarChartModel horizontalBarModel;

    @PostConstruct()
    public void init() {
        createPieModel();
        initBarModel();
    }

    private void createPieModel() {

        pieModel = initPieModel();

        pieModel.setTitle("Umsetzungstatus Hamburg");
        pieModel.setLegendPosition("w");
        pieModel.setExtender("skinPie");

        initPieModel();
    }

    private PieChartModel initPieModel() {
        PieChartModel model = new PieChartModel();

        Map<String, Number> states = controlService.aggregateMassnahmenUmsetzungStatus();
        states = setLabel(states);
        model.setData(states);
        return model;
    }

    private Map<String, Number> setLabel(Map<String, Number> states) {
        Map<String, Number> humanReadableLabels = new HashMap<>();
        for(Entry<String, Number> e : states.entrySet()){
            humanReadableLabels.put(getLabel(e), e.getValue());
        }

        return humanReadableLabels;
    }

    private HorizontalBarChartModel initBarModel() {

        setHorizontalBarModel(new HorizontalBarChartModel());
        Map<String, Number> states = controlService.aggregateMassnahmenUmsetzungStatus();

        for (Map.Entry<String, Number> entry : states.entrySet()) {
            ChartSeries boys = new ChartSeries();
            boys.getRenderer();
            String label = getLabel(entry);
            boys.set(label, entry.getValue());
            getHorizontalBarModel().addSeries(boys);
        }

        getHorizontalBarModel().setLegendPosition("e");

        Axis xAxis = getHorizontalBarModel().getAxis(AxisType.X);
        xAxis.setLabel("Anzahl");
        xAxis.setMin(0);
        xAxis.setMax(200);

        Axis yAxis = getHorizontalBarModel().getAxis(AxisType.Y);
        yAxis.setLabel("Status");
        return getHorizontalBarModel();
    }

    private String getLabel(Map.Entry<String, Number> entry) {

        if(MassnahmenUmsetzung.P_UMSETZUNG_UNBEARBEITET.equals(entry.getKey())) {
            return Messages.getString(IMPLEMENTATION_STATUS_UNEDITET);
        }

        return getPropertyName().getLabel(entry.getKey());
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public ControlService getControlService() {
        return controlService;
    }

    public void setControlService(ControlService controlService) {
        this.controlService = controlService;
    }

    public HorizontalBarChartModel getHorizontalBarModel() {
        return horizontalBarModel;
    }

    public void setHorizontalBarModel(HorizontalBarChartModel horizontalBarModel) {
        this.horizontalBarModel = horizontalBarModel;
    }

    private IObjectModelService getPropertyName() {
        return (IObjectModelService) VeriniceContext.get(VeriniceContext.OBJECT_MODEL_SERVICE);
    }
}

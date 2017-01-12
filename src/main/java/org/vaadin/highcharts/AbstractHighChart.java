package org.vaadin.highcharts;

import com.vaadin.ui.AbstractJavaScriptComponent;

public abstract class AbstractHighChart extends AbstractJavaScriptComponent {

    private static final long serialVersionUID = 7738496276049495017L;
    private static int currChartId = 0;

    public AbstractHighChart() {
        setId("highchart_" + currChartId++);
        getState().domId = getId();
    }

    @Override
    protected HighChartState getState() {
        return (HighChartState) super.getState();
    }

    public void setScreenName(String screenName) {
        getState().screenName = screenName;
    }
}

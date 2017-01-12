package org.vaadin.highcharts;

import org.vaadin.highcharts.AbstractHighChart;

import com.vaadin.annotations.JavaScript;

@JavaScript({ "jquery-3.1.1.min.js", "highstock.js", "highcharts-more.js", "highcharts-connector.js" })
public class StockChart extends AbstractHighChart {

    private static final long serialVersionUID = -7326315426217377755L;
}

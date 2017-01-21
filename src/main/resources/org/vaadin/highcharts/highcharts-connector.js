window.org_vaadin_highcharts_AbstractHighChart = function() {

    this.onStateChange = function() {

        var domId = this.getState().domId;
        var screenName = this.getState().screenName;

        console.log('Screen name: ' + screenName);

        if (screenName) {
            $.getJSON('/data?name=' + screenName + '&callback=?', function(data) {
                Highcharts.chart(domId, {
                    chart: {
                        backgroundColor: 'transparent'
                    },
                    title: {
                        text: null
                    },
                    credits: {
                        enabled: false
                    },
                    xAxis: {
                        type: 'datetime'
                    },
                    tooltip: {
                        pointFormat: '<b>{point.y}</b> followers'
                    },
                    series : [ {
                        showInLegend: false,
                        data : data
                    } ]
                });
            });
        }
    };
};
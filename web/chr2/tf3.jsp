<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>Demo: Columns - Monthly Sales with Highlights</title>
        <link rel="stylesheet" href="/dojo/1.10.4/dijit/themes/claro/claro.css" media="screen">
        <style>
        /*.dojoxLegendNode {border: 1px solid #ccc; margin: 5px 10px 5px 10px; padding: 3px}*/
        /*.dojoxLegendText {vertical-align: text-top; padding-right: 10px}*/
        </style>
	</head>

	<body class="claro">
		<h1>Demo: Columns6</h1>

		<div id="chartNode" style="width:400px;height:400px;"></div>
        <div id="legend3"></div>
		<!-- load dojo and provide config via data attribute -->
        <script>
            dojoConfig = {
            parseOnLoad: true,
            baseUrl: '/dojo/1.10.4/',
            modulePaths: {
            "dojo": "dojo",
            "dojox": "dojox",
             "dijit":"dijit"
            }
        };
        </script>
		<script src="/dojo/1.10.4/dojo/dojo.js"></script>
		<script>

require([
    "dojo/_base/declare",
    "dojox/charting/Chart",
    "dojox/charting/plot2d/Pie",
    "dojox/charting/themes/Claro",
    "dojox/charting/action2d/MoveSlice"
], function (declare, Chart, Pie, theme, MoveSlice) {

    var Donut = declare(Pie, {
        render: function (dim, offsets) {
            // Call the Pie's render method
            this.inherited(arguments);

            // Draw a white circle in the middle
            var rx = (dim.width - offsets.l - offsets.r) / 2,
                ry = (dim.height - offsets.t - offsets.b) / 2,
                r = Math.min(rx, ry) / 2,
                circle = {
                    cx: offsets.l + rx,
                    cy: offsets.t + ry,
                    r: r
                },
                s = this.group;

            s.createCircle(circle).setFill("#fff").setStroke("#fff");
        }
    });

    // Create the chart within it's "holding" node
    var pieChart = new Chart("chartNode"),
        chartData = [{
            x: 1,
            y: 19021
        }, {
            x: 2,
            y: 12837
        }, {
            x: 4,
            y: 12378
        }, {
            x: 1,
            y: 21882
        }, {
            x: 1,
            y: 17654
        }, {
            x: 1,
            y: 15833
        }, {
            x: 1,
            y: 16122
        }];

    // Set the theme
    pieChart.setTheme(theme);

    // Add the only/default plot
    pieChart.addPlot("default", {
        type: Donut, // our plot2d/Pie module reference as type value
        radius: 200,
        fontColor: "black",
        labelOffset: -20
    });

    // Add the series of data
    pieChart.addSeries("January", chartData);

    // Animate the donut slices
    new MoveSlice(pieChart,"default");
    // Render the chart!
    pieChart.render();
});
		</script>
	</body>
</html>

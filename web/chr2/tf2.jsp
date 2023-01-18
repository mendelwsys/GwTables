<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
	<head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=11; IE=10; IE=8" />
		<title>Demo: Columns - Monthly Sales with Highlights</title>
        <link rel="stylesheet" href="/dojo/1.10.4/dijit/themes/claro/claro.css" media="screen">
        <style>
        /*.dojoxLegendNode {border: 1px solid #ccc; margin: 5px 10px 5px 10px; padding: 3px}*/
        /*.dojoxLegendText {vertical-align: text-top; padding-right: 10px}*/
        </style>
	</head>

	<body class="claro">
		<%--<h1>Demo: Charts27</h1>--%>
		<%--<div id="chartNode" style="width:400px;height:400px;"></div>--%>
		<div id="chartNode"></div>
        <%--<div id="legend3"></div>--%>
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
			 // Require the basic chart class
			"dojox/charting/Chart",
			// Require the theme of our choosing
			//"dojox/charting/themes/PlotKit/green",
             "dojox/charting/themes/ThreeD",
			// Charting plugins:
			// 	We want to plot ....
			//"dojox/charting/plot2d/Columns",
            //"dojox/charting/plot2d/Bars"
            "dojox/charting/plot2d/ClusteredColumns",
            "dojox/charting/plot2d/Pie",
            // Retrieve the Tooltip classes
			"dojox/charting/action2d/Tooltip",
			// Retrieve the MoveSlice classes
            "dojox/charting/action2d/MoveSlice",
			// Retrieve the Highlight classes
			"dojox/charting/action2d/Highlight",
            // Retrieve the Tooltip classes
            "dojox/charting/widget/Legend",
            "dojox/charting/Theme",
			//	We want to use Markers
			"dojox/charting/plot2d/Markers",
			//	We'll use default x/y axes
			"dojox/charting/axis2d/Default",
			//	We'll plot Grid
            "dojox/charting/plot2d/Grid",
			// Wait until the DOM is ready
			"dojo/domReady!"
		], function(declare,Chart, theme, ColumnsPlot,Pie,
                    Tooltip, MoveSlice,
                    Highlight, Legend,SimpleTheme
                ) {

            var bindName=<%out.print("'"+request.getParameter("bindName")+"'");%>;
            var bindObject;
            if (window.parent)
              bindObject=window.parent.binding[bindName];
            else
              bindObject=window.binding[bindName];

            if (!bindObject)
                alert("bindObject is null");
            else
            {

//                (function(){
//                    theme = new SimpleTheme({
//                    colors: [
//                        "#FF0000",
//                        "#DD1010",
//                        "#BB2020",
//                        "#903030",
//                        "#703030"
//                    ]
//                });
//                })();



                bindObject.Chart=Chart;
                bindObject.theme=theme;
                //bindObject.chart=new Chart("chartNode");
                //bindObject.chart.setTheme(theme);

                //bindObject.Donut = declare('dojox.charting.plot2d.Donut2d',Pie, {

                bindObject.Donut = declare(Pie, {
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
                bindObject.Pie=Pie;
                bindObject.ColumnsPlot=ColumnsPlot;
                bindObject.Tooltip=Tooltip;
                bindObject.MoveSlice=MoveSlice;
                bindObject.Highlight=Highlight;
                bindObject.Legend=Legend;

                bindObject.convArray= function(chartData1)
                {
                    var chartData=new Array();
                    for (var ix=0;ix<chartData1.length;ix++)
                        chartData.push(chartData1[ix]);
                    return chartData;
                };

                bindObject.testRender = function(chartData)
                {

                    bindObject.chart=new bindObject.Chart("chartNode");

                    var chart=bindObject.chart;
                    // Add the only/default plot
                    chart.addPlot("default", {
                        type: bindObject.ColumnsPlot,
                        markers: true,
                        gap: 10
                    });
                    //Add axes
                    chart.addAxis("x",{ microTickStep: 1, minorTickStep: 1, max: 20 });
                    chart.addAxis("y", { vertical: true, fixLower: "major", fixUpper: "major" });

                    // Add  Grid
                    chart.addPlot("grid", {type: "Grid"});
                    // Add the series of data
                    chart.addSeries("Monthly Sales",chartData,{stroke: {color: "black"}, fill: "red"});

                    // Create the tooltip
                    new bindObject.Tooltip(chart,"default");

                    new bindObject.Highlight(chart,"default",{
                        highlight: "gold"
                    });
                    // Render the chart!
                    chart.render();
                    var legend3 = new bindObject.Legend({chart: chart, horizontal: false}, "legend3");
                }
            }
		});
		</script>
	</body>
</html>

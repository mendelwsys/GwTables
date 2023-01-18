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

		<div id="chartNode" style="width:800px;height:200px;"></div>
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
			 // Require the basic chart class
			"dojox/charting/Chart",
			// Require the theme of our choosing
			"dojox/charting/themes/PlotKit/green",
			// Charting plugins:
			// 	We want to plot ....
			//"dojox/charting/plot2d/Columns",
            "dojox/charting/plot2d/ClusteredColumns",
            //"dojox/charting/plot2d/Bars"

            // Retrieve the Tooltip classes
			"dojox/charting/action2d/Tooltip",
			// Retrieve the MoveSlice classes
//            "dojox/charting/action2d/MoveSlice",

			// Retrieve the Highlight classes
			"dojox/charting/action2d/Highlight",
            // Retrieve the Tooltip classes
            "dojox/charting/widget/Legend",
			//	We want to use Markers
			"dojox/charting/plot2d/Markers",
			//	We'll use default x/y axes
			"dojox/charting/axis2d/Default",
			//	We'll plot Grid
            "dojox/charting/plot2d/Grid",
			// Wait until the DOM is ready
			"dojo/domReady!"
		], function(Chart, theme, ColumnsPlot,
                    Tooltip,
//                    MoveSlice
                    Highlight, Legend
                ) {

			// Define the data
			var chartData = [10000,9200,11811,12000,7662,13887,14200,12222,12000,10009,11288,12099];

			// Create the chart within it's "holding" node
			var chart = new Chart("chartNode");

			// Set the theme
			chart.setTheme(theme);

			// Add the only/default plot
			chart.addPlot("default", {
				type: ColumnsPlot,
				markers: true,
				gap: 10
			});

			// Add axes
			chart.addAxis("x",{ microTickStep: 1, minorTickStep: 1, max: 20 });
			chart.addAxis("y", { vertical: true, fixLower: "major", fixUpper: "major" });

            chart.addPlot("grid", {type: "Grid"});
			// Add the series of data
			chart.addSeries("Monthly Sales",chartData,{stroke: {color: "black"}, fill: "red"});
                                                                                         //,{stroke: {color: "black"}, fill: "blue"},{stroke: {color: "black"}, fill: "red"}

//            new MouseZoomAndPan(chart, "default", { axis: "y"});

			// Create the tooltip
			var tip = new Tooltip(chart,"default");

            // Create the slice mover
//			var mag = new MoveSlice(chart,"default");

//			Highlight!
			new Highlight(chart,"default",{
		        highlight: "gold"
	        });

			// Render the chart!
			chart.render();
            var legend3 = new Legend({chart: chart, horizontal: false}, "legend3");

//            var interval = setInterval(function()
//            {
//                for (var i = 0; i < chartData.length; i++)
//                {
//                    if (startNumber%2==0)
//                        chartData[i]-=Math.ceil(Math.random()*2000);
//                    else
//                       chartData[i]+=Math.ceil(Math.random()*2000);
//                }
//
//                startNumber++;
//    //            chart.resize((width-startNumber*10), (height+startNumber*10));
//                chart.updateSeries("Monthly Sales",chartData);
//                chart.render();
//                if(startNumber == 120) clearInterval(interval);
//            },1000);

            //
            startNumber=0;
            var reRender=function()
            {
                //alert(window.parent.binding['pane1']);
                for (var i = 0; i < chartData.length; i++)
                {
                    if (startNumber%2==0)
                        chartData[i]-=Math.ceil(Math.random()*2000);
                    else
                       chartData[i]+=Math.ceil(Math.random()*2000);
                }

                startNumber++;
                chart.updateSeries("Monthly Sales",chartData);
                chart.render();

            };
            if (window.parent)
              window.parent.binding[<%out.print("'"+request.getParameter("bindName")+"'");%>].testRender=reRender;
            else
                window.binding[<%out.print("'"+request.getParameter("bindName")+"'");%>].testRender=reRender;
		});


		</script>
	</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
	<head>
	 <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=11; IE=10; IE=8" />
	<title>Base chart loading library</title>
        <link rel="stylesheet" href="/dojo/1.10.4/dijit/themes/claro/claro.css" media="screen">
        <style>
        /*.dojoxLegendNode {border: 1px solid #ccc; margin: 5px 10px 5px 10px; padding: 3px}*/
        /*.dojoxLegendText {vertical-align: text-top; padding-right: 10px}*/
        </style>

        <script language="JavaScript">
//TODO !!!Пипец!!!!
//            dojoConfig = {
//                gfxRenderer: "silverlight,svg,vml" // svg gets priority
//            };
            var bindObject4Unload;
            function unloadFunction()
            {
                if (bindObject4Unload)
                {
                    bindObject4Unload.wasLoad=false;
                    bindObject4Unload.wasInit=false;
                    bindObject4Unload.chart=null;
                    bindObject4Unload.chartTypes=new Array();
//                    bindObject4Unload.unloadCtrl='Hello';
                }
            }


        </script>
	</head>

    <%--//TODO Сделать относительно корня /dojo/1.10.4/!!!!--%>
	<body class="claro" onunload="unloadFunction()">
		<%--<h1>Demo: Charts27</h1>--%>
		<%--<div id="chartNode" style="width:400px;height:400px;"></div>--%>
		<div id="chartNode"></div>
        <%--<div id="legend3"></div>--%>
		<!-- load dojo and provide config via data attribute -->
        <script>

            dojoConfig = {
            parseOnLoad: true,
            baseUrl: '<%out.print(request.getContextPath());%>/dojo/1.10.4/',

            modulePaths: {
            "dojo": "dojo",
            "dojox": "dojox",
             "dijit":"dijit"
            }
        };
        </script>
		<script src="<%out.print(request.getContextPath());%>/dojo/1.10.4/dojo/dojo.js"></script>
		<script>


		require([
            "dojo/_base/declare",
            "dojo/_base/lang",
			 // Require the basic chart class
			"dojox/charting/Chart",
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
            //Тестовые данные
            "dojox/charting/themes/ThreeD",
			// Wait until the DOM is ready
			"dojo/domReady!"
		], function(declare,lang,Chart,Tooltip, MoveSlice,
                    Highlight, Legend,Theme,Markers,DefaultXY,GridPlot,
                    defaultTheme
                ) {

            var bindName=<%
            out.print("'"+request.getParameter("bindName")+"'");
            %>;
            var bindObject;
            if (window.parent)
              bindObject=window.parent.binding[bindName];
            else
              bindObject=window.binding[bindName];

            if (!bindObject)
                alert("bindObject is null");
            else
            {
//                unloadFunction(bindObject);

                bindObject.lang=lang;
                bindObject.libRequire=function(libName)
                {
                    dojo.require(libName);
                }

                bindObject.declare=declare;
                bindObject.Chart=Chart;
                bindObject.Tooltip=Tooltip;
                bindObject.MoveSlice=MoveSlice;
                bindObject.Highlight=Highlight;
                bindObject.Legend=Legend;
                bindObject.Theme=Theme;
                bindObject.Markers=Markers;
                bindObject.DefaultXY=DefaultXY;
                bindObject.GridPlot=GridPlot;

                bindObject.defaultTheme=defaultTheme;

                bindObject.chartTypes=new Array();

                bindObject.copyArray= function(chartData1)
                {
                    var chartData=new Array();
                    for (var ix=0;ix<chartData1.length;ix++)
                        chartData.push(chartData1[ix]);
                    return chartData;
                };
                bindObject4Unload=bindObject;   //Подпорка для отлавливания события выгрузки, дело в том что во время обработки onunload не удается обратиться к массиву window.parent.binding - он уже не определен.
                bindObject.wasLoad=true;
            }
		});
		</script>
	</body>
</html>

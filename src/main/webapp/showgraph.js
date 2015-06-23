var width = 1000, height = 300;
var minW = 100, minH = 50;
var mt = 20, mb = 20, ml = 10, mr = 10;

var color = ["#ff9933", "#ffa500", "#cc0000", "#ff0033", "#cccc00", "#cc0066", "#cc3333", "#ff3399", "#cc6600", "#993300", "#ff9966", "#ff3300", "#ffff00", "#955251", "#f79256", "#c78d6b"];
var colorOnMouse = ["#cf7317", "#cf7e00", "#a10000", "#cf0017", "#a1a100", "#a10045", "#a11717", "#cf1773", "#a14500", "#731700", "#cf7345", "#cf1700", "#cfcf00", "#6e3232", "#c86c37", "#9c6749"];
var nonClone = "#00b2ca";
var nonCloneOnMouse = "#008a9e";

function calcX(xIndex, revs, w) {
	return Math.floor(ml + (xIndex + 1) * (w - ml - mr) / (revs + 1));
}

function calcY(yIndex, lines, h) {
	return Math.floor(mt + (yIndex + 1) * (h - mt - mb) / (lines + 1));
}

function load(id) {
	d3.select("#graphContainer").selectAll("*").remove();
	d3.select("#graphContainer").style("background-color", "rgba(255, 255, 255, 0.7)");
	width = window.innerWidth * 0.7;
	height = window.innerHeight * 0.35;

	d3.json("/freatdata?genealogy=" + id, function(err, data) {
		var w = Math.max(width, minW);
		var h = Math.max(height, minH);

		var svg = d3.select("#graphContainer").append("svg").attr("width", w).attr("height", h);
		//svg.append("rect").attr("fill", "rgba(255, 255, 255, 0.7)").attr("width", w - 2).attr("height", h - 2);
		svg.append("text").text("genealogy id: " + id).attr("x", 10).attr("y", 20);

		svg.selectAll("line").data(data.links).enter().append("line").attr("class", "link").attr("x1", function(d) {
			return calcX(d.beforeX, data.revs, w);
		}).attr("y1", function(d) {
			return calcY(d.beforeY, data.lines, h);
		}).attr("x2", function(d) {
			return calcX(d.afterX, data.revs, w);
		}).attr("y2", function(d) {
			return calcY(d.afterY, data.lines, h);
		}).attr("stroke", function(d) {
			if (d.changed) {
				/*return "#00552e";*/
				return "#e2041b";
			} else {
				return "#5b6356";
			}
		}).attr("stroke-width", function(d) {
			if (d.changed) {
				return "5px";
			} else {
				return "1.5px";
			}
		}).attr("stroke-dasharray", function(d) {
			if (!d.changed) {
				return "none";
			} else {
				return "4 2";
			}
			return "none";
		});

		svg.selectAll("circle").data(data.nodes).enter().append("circle").attr("class", "node").attr("r", function(d) {
			return Math.min(20, Math.floor((h - mt - mb) / (4 * data.lines)), Math.floor((w - ml - mr) / (4 * data.revs)));
		}).attr("cx", function(d) {
			return calcX(d.xIndex, data.revs, w);
		}).attr("cy", function(d) {
			return calcY(d.yIndex, data.lines, h);
		}).attr("fill", function(d) {
			if (d.inClone < 0) {
				return nonClone;
			} else {
				var colorIndex = d.inClone % color.length;
				return color[colorIndex];
			}
		}).attr("id", function(d) {
			return "frag-" + d.fragmentId;
		}).on("mouseover", function(d) {
			d3.select(this).attr("fill", function(d) {
				if (d.inClone < 0) {
					return nonCloneOnMouse;
				} else {
					var colorIndex = d.inClone % colorOnMouse.length;
					return colorOnMouse[colorIndex];
				}
			}).append("title").text(function(d) {
				return d.repoName + " (Rev." + d.rev + ")\n" + d.path + "\nlines: " + d.startLine + "-" + d.endLine;
			});
		}).on("mouseout", function(d) {
			d3.select(this).attr("fill", function(d) {
				if (d.inClone < 0) {
					return nonClone;
				} else {
					var colorIndex = d.inClone % color.length;
					return color[colorIndex];
				}
			});
			d3.select(this).selectAll("title").remove();
		}).on("click", function() {
			var fragmentId = d3.select(this).attr("id").split("-")[1];
			setSrc(fragmentId);
		});
	});

}


/*$("scg circle").tipsy({
	gravity : 'w',
	fade : true
});*/ 
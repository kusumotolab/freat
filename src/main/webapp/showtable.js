var width = 1000, height = 300;
var mt = 20, mb = 20, ml = 10, mr = 10;
var minW = 50, minH = 100;

var headerData = ["id", "start", "C-end", "F-end", "ghost", "#frag", "#repo"];

loadTable("all");

function loadTable(query) {
	d3.select("#genealogyTable").selectAll("*").remove();
	
	width = window.innerWidth * 0.25;
	height = window.innerHeight * 0.95;
	var w = Math.max(width, minW);
	var h = Math.max(height, minH);

	d3.select("#genealogyTable").style({
		"width" : w + "px",
		"height" : h + "px"
	});

	var table = d3.select("#genealogyTable").append("table").attr("class", "bordered");
	var thead = table.append("thead");
	var tbody = table.append("tbody");

	thead.append("tr").selectAll("th").data(headerData).enter().append("th").text(function(d) {
		return d;
	});

	query = query.trim();
	if (!query) {
		query = "all";
	}

	d3.json("/freatdata?query=" + query, function(err, data) {
		var tr_td = tbody.selectAll("tr").data(data.genealogies).enter().append("tr").style("background-color", "rgba(255, 255, 255, 0.75)").attr("id", function(d) {
			return d.id;
		});
		var td = tr_td.selectAll("td").data(function(d) {
			return d3.values(d);
		}).enter().append("td").text(function(d) {
			return d;
		});
		tr_td.on("mouseover", function() {
			d3.select(this).style("background-color", "rgba(0, 100, 0, 0.2)");
		}).on("mouseout", function() {
			d3.select(this).style("background-color", "rgba(255, 255, 255, 0.7)");
		}).on("click", function(d) {
			d3.select("#sourceContainer").selectAll("*").remove();
			var idstr = d3.select(this).select("td").text();
			load(idstr);
		});
	});
}


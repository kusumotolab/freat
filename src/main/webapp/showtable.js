var w = 1000, h = 300;
var mt = 20, mb = 20, ml = 10, mr = 10;

var headerData = ["id", "start", "end", "revs"];

loadTable();

function loadTable() {
	var table = d3.select("#genealogyTable").append("table");
	var thead = table.append("thead");
	var tbody = table.append("tbody");

	thead.append("tr").selectAll("th").data(headerData).enter().append("th").text(function(d) {
		return d;
	});

	d3.json("/tabledata", function(err, data) {
		/*
		 for (var i = 0; i < data.genealogies.length; i++) {
		 var tr = tbody.append("tr").attr("id", data.genealogies[i].id);
		 tr.append("td").text(data.genealogies[i].id);
		 tr.append("td").text(data.genealogies[i].startRevId);
		 tr.append("td").text(data.genealogies[i].endRevId);
		 tr.append("td").text(data.genealogies[i].numRevisions);
		 tr.on("mouseover", function() {
		 d3.select("this").attr("fill", "rgb(0, 100, 0, 0.2)");
		 });
		 }
		 */

		var tr_td = tbody.selectAll("tr").data(data.genealogies).enter().append("tr").attr("id", function(d) {
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
			d3.select(this).style("background-color", "rgb(255, 255, 255)");
		}).on("click", function(d) {
			var idstr = d3.select(this).select("td").text();
			clickRow(idstr);
		});
	});
}

function clickRow(idstr) {
	/*
	 d3.select("#textContainer").selectAll("*").remove();
	 d3.select("#textContainer").append("p").text("genealogy id: " + idstr);
	 */
	load(idstr);
}

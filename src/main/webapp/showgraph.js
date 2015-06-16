var w = 1000, h = 300;
var mt = 20, mb = 20, ml = 10, mr = 10;

function calcX(xIndex, revs) {
	return Math.floor(ml + (xIndex + 1) * (w - ml - mr) / (revs + 1));
}

function calcY(yIndex, lines) {
	return Math.floor(mt + (yIndex + 1) * (h - mt - mb) / (lines + 1));
}

function load(id) {
	d3.select("#graphContainer").selectAll("*").remove();

	d3.json("/genealogy?id=" + id, function(err, data) {
		var svg = d3.select("#graphContainer").append("svg").attr("width", w).attr("height", h);
		svg.append("text").text("genealogy id: " + id).attr("x", 10).attr("y", 20);

		svg.selectAll("line").data(data.links).enter().append("line").attr("class", "link").attr("x1", function(d) {
			return calcX(d.beforeX, data.revs);
		}).attr("y1", function(d) {
			return calcY(d.beforeY, data.lines);
		}).attr("x2", function(d) {
			return calcX(d.afterX, data.revs);
		}).attr("y2", function(d) {
			return calcY(d.afterY, data.lines);
		});

		svg.selectAll("circle").data(data.nodes).enter().append("circle").attr("class", "node").attr("r", function(d) {
			return Math.min(20, Math.floor((h - mt - mb) / (4 * data.lines)), Math.floor((w - ml - mr) / (4 * data.revs)));
		}).attr("cx", function(d) {
			return calcX(d.xIndex, data.revs);
		}).attr("cy", function(d) {
			return calcY(d.yIndex, data.lines);
		}).attr("fill", "orange").attr("id", function(d) {
			return "frag-" + d.fragmentId;
		}).on("mouseover", function() {
			d3.select(this).attr("fill", "red");
		}).on("mouseout", function() {
			d3.select(this).attr("fill", "orange");
		}).on("click", function() {
			var fragmentId = d3.select(this).attr("id").split("-")[1];
			setLeftSrc(fragmentId);
		});
	});

}
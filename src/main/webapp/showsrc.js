var width = 1000, height = 350;
var minW = 100, minH = 50;

function setSrc(fragmentId) {
	d3.select("#sourceContainer").selectAll("*").remove();
	width = window.innerWidth * 0.7;
	height = window.innerHeight * 0.45;

	var w = Math.max(width, minW);
	var h = Math.max(height, minH);
	
	d3.select("#sourceContainer").style({
		"width": w + "px",
		"height": h + "px"
	});

	d3.json("/fragment?id=" + fragmentId, function(err, data) {
		var brush = new SyntaxHighlighter.brushes.Java(), code = data.src;
		var lines = [];
		for (var i = data.startLine; i <= data.endLine; i++) {
			lines.push(i);
		}
		brush.init({
			toolbar : false,
			title : data.filePath + "(Rev." + data.revision + ")",
			highlight : lines,
		});
		var html = brush.getHtml(code);
		document.getElementById("sourceContainer").innerHTML = html;
	});

	//SyntaxHighlighter.all();

}
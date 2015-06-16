function setLeftSrc(fragmentId) {
	d3.select("#sourceContainerLeft").selectAll("*").remove();

	d3.json("/fragment?id=" + fragmentId, function(err, data) {
		var brush = new SyntaxHighlighter.brushes.Java(),
			code = data.src;
		brush.init({ toolbar: false});
		var html = brush.getHtml(code);
		//d3.select("#sourceContainerLeft").append("pre").attr("class", "brush: java;").text(data.src);
		//d3.select("#sourceContainerLeft").append("div").text(html);
		document.getElementById("sourceContainerLeft").innerHTML = html;
	});
	
	SyntaxHighlighter.all();

}
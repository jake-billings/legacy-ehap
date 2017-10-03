function refresh(secs) {
	setTimeout(function() {
		history.go(0);
	},secs*1000);
}
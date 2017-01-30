/**
 * Shows the code block that corresponds to the @clickedTab tab,
 * hides the others.
 */
function __showCodeBlock(clickedTab) {
	var tabs = clickedTab.parent().children();
	var currentCodeBlock = clickedTab.parent().next();

	tabs.each(function() {
		var currentTab = $(this);

		if (currentTab.is(clickedTab)) {
			currentTab.addClass("selected");
			currentCodeBlock.show();
		} else {
			currentTab.removeClass("selected");
			currentCodeBlock.hide();
		}

		currentCodeBlock = currentCodeBlock.next();
	});
}

/* Executed whenever a user clicks a code selector tab. */
function showCodeBlock(clickedTab) {
	__showCodeBlock($(clickedTab));
}

function initCodeAlternatives() {
	$(".codeblock-menu").each(function() {
		__showCodeBlock($(this).find(":first-child"));
	});
}

window.onload = function() {
	initCodeAlternatives();
};


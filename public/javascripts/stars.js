// Lorsque le DOM est chargé on applique le Javascript 
$(document).ready(function() {
	// On ajoute la classe "js" à la liste pour mettre en place par la suite du code CSS uniquement dans le cas où le Javascript est activé
	$("ul.notes-echelle").addClass("js");
	// On passe chaque note à l'état grisé par défaut
	$("ul.notes-echelle li").addClass("note-off");
	// Au survol de chaque note à la souris
	$("ul.notes-echelle li").mouseover(function() {
		// On passe les notes supérieures à l'état inactif (par défaut)
		$(this).nextAll("li").addClass("note-off");
		// On passe les notes inférieures à l'état actif
		$(this).prevAll("li").removeClass("note-off");
		// On passe la note survolée à l'état actif (par défaut)
		$(this).removeClass("note-off");
	});
	// Lorsque l'on sort du sytème de notation à la souris
	$("ul.notes-echelle").mouseout(function() {
		// On passe toutes les notes à l'état inactif
		$(this).children("li").addClass("note-off");
		// On simule (trigger) un mouseover sur la note cochée s'il y a lieu
		$(this).find("li input:checked").parent("li").trigger("mouseover");
	});
	
	$("label.starsclickable").click(function()	{
		var jqxhrMark = $.post("/takeAMark", { "mark":$(this).text(), "pro":$("#id").val() }, function(data) {
			if(data.status == "success")	{
				$("#averageNote").html(data.average);
			}
		}, "json");
	});
});
function updateTimeTags(selector, date) {
  console.log("Updating content of <time> tags");
  var toString = date ? "toLocaleDateString" : "toLocaleString";
  $(selector).each(function() {
    var datetime = $(this).attr('datetime');
    if (datetime) {
      $(this).text(new Date(datetime)[toString]());
    }
  });
}


updateTimeTags('.localDateTime', true);

// find navbar item with a link that leads to the current URL and
// apply .active class to it

$('[href="' + document.location.pathname + '"]').parent().addClass('active');

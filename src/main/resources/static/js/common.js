function updateTimeTags(selector, date) {
  console.log("Updating content of <time> tags");

  var options = {
    year: "numeric", month: "2-digit", day: "2-digit", hour: "2-digit",
    minute: "2-digit"
  };
  if (date) {
    options = {year: "numeric", month: "long", day: "numeric"};
  }

  var formatter = new Intl.DateTimeFormat(undefined, options);
  $(selector).each(function() {
    var datetime = $(this).attr('datetime');
    if (datetime) {
      $(this).text(formatter.format(new Date(datetime)));
    }
  });
}


updateTimeTags('.localDateTime', true);

// find navbar item with a link that leads to the current URL and
// apply .active class to it

$('[href="' + document.location.pathname + '"]').parent().addClass('active');

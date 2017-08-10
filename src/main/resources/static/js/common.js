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

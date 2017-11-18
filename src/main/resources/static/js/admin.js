(function setLocalPublicationDate() {
  console.log(
    'Setting value of local publication datetime field...');
  var currentInstant = $('#publicationTimestamp').val();
  if (currentInstant) {
    var ldt = new Date(currentInstant);
    ldt.setMinutes(ldt.getMinutes() - ldt.getTimezoneOffset());
    $('#localPublicationDate').val(ldt.toJSON().slice(0, 16));
  }
})();


$('#articleForm').submit(function() {
  console.log('Setting value of publicationTimestamp field...');
  var localValue = $('#localPublicationDate').val();
  var instant = $('#publicationTimestamp');
  instant.val('');
  if (localValue) {
    console.log('Local publication datetime is not empty' +
        ', setting publicationTimestamp');
    instant.val(new Date(localValue).toISOString());
  }
});


updateTimeTags('.articleDateTime', false);


function ArticleIdData(jqButton) {
  this.id = jqButton.val();
  var tableRow = jqButton.closest('tr');
  if (tableRow[0]) {
    this.title = tableRow.find('.articleTitle').text();
  } else {
    this.title = $('#title').val();
  }
}

ArticleIdData.prototype.toString = function() {
  return 'article "' + this.title + '" (id: ' + this.id + ')';
};


function YuleDialog(selector) {
  this.jqDialog = $(selector);
}

YuleDialog.prototype.setMessage = function(message) {
  this.jqDialog.find('p').text(message);
};


YuleDialog.prototype.show = function(message) {
  this.setMessage(message);
  this.jqDialog.modal('show');
};

YuleDialog.prototype.hide = function() {
  this.jqDialog.modal('hide');
};


var messageDialog = new YuleDialog('#messageDialog');


var requestConfirmationDialog = new YuleDialog('#confirm');


requestConfirmationDialog.disableButtons = function(disabled) {
  this.jqDialog.find('button').each(function() {
    $(this).attr('disabled', disabled);
  });
};

requestConfirmationDialog.show = function(message) {
  this.disableButtons(false);
  YuleDialog.prototype.show.call(this, message);
};


var task = {
  initialMessage: null,
  postConfirmationMessage: null,
  description: null,
  _execute: null,
  dialog: requestConfirmationDialog,

  init: function(params) {
    console.log('Initializing: ' + params.description);
    this.initialMessage = params.initialMessage;
    this.postConfirmationMessage = params.postConfirmationMessage;
    this.description = params.description;
    this._execute = params.execute;

    this.dialog.show(this.initialMessage);
  },

  cancel: function() {
    console.log('Cancelling ' + this.description);
    for (var p in this) {
      if (this.hasOwnProperty(p)) {
        p = null;
      }
    }
  },

  execute: function() {
    console.log('Executing ' + this.description);
    this.dialog.disableButtons(true);
    this.dialog.setMessage(this.postConfirmationMessage);
    this._execute();
  }
};


// .deleteArticle click handler (shows delete dialog)
$('.deleteArticle').each(function() {
  $(this).click(function(){
    var article = new ArticleIdData($(this));

    function sendDeleteArticleRequest() {
      var csrfToken = $("meta[name='_csrf']").attr("content");
      if (!csrfToken) {
        csrfToken = $('input[name="_csrf"]').val();
      }
      console.log('Sending request for deleting ' + article);
      $('<form action=/admin/article/' + article.id +
        '/delete method="POST" style="display=none;">' +
        '<input type="hidden" name="_csrf" value="' +
        csrfToken + '"></form>').appendTo('body').submit();
    }

    task.init({
      initialMessage: 'Are you sure you want to remove the article "' +
      article.title + '"?',

      postConfirmationMessage: 'Deleting the article: "' + article.title +
      '"...',

      description: 'Sending request to delete ' + article,
      execute: sendDeleteArticleRequest
    });
  });
});


$('#confirmRequest').click(function() {
  task.execute();
});

$('#cancelRequest').click(function() {
  task.cancel();
});


function togglePublicationTimeHint() {
  var value = $('#status').val();
  var hint = $('#publicationTimeHint');
  if (value === 'PUBLISHED') {
    hint.show();
  } else {
    hint.hide();
  }
}

togglePublicationTimeHint();
$('#status').change(togglePublicationTimeHint);



if (typeof HTMLDialogElement !== 'function') {
  $('dialog').each(function() {
    dialogPolyfill.registerDialog($(this));
  });
}


(function setLocalPublicationDate() {
  console.log('Setting value of local publication date field...');
  var currentInstant = $('#publicationDate').val();
  if (currentInstant) {
    var ldt = new Date(currentInstant);
    ldt.setMinutes(ldt.getMinutes() - ldt.getTimezoneOffset());
    $('#localPublicationDate').val(ldt.toJSON().slice(0, 16));
  }
})();


$('#articleForm').submit(function() {
  console.log('Setting value of publicationDate field...');
  var localValue = $('#localPublicationDate').val();
  var instant = $('#publicationDate');
  instant.val('');
  if (localValue) {
    console.log('Local publication date is not empty' +
        ', setting publicationDate');
    instant.val(new Date(localValue).toISOString());
  }
});


updateTimeTags('.articleDateTime', false);


function YuleDialog(selector) {
  this.jqDialog = $(selector);
}

YuleDialog.prototype.showModal = function(message) {
  this.jqDialog.find('p').text(message);
  this.jqDialog[0].showModal();
};

YuleDialog.prototype.close = function() {
  this.jqDialog[0].close();
};


var messageDialog = new YuleDialog('#messageDialog');

messageDialog.showModal = function(message, hideCloseButton) {
  var closeButton = $('#closeMessageDialog');
  if (!hideCloseButton) {
    closeButton.show();
  } else {
    closeButton.hide();
  }
  YuleDialog.prototype.showModal.call(this, message);
};

$('#closeMessageDialog').click(function() {
  console.log('Closing message dialog');
  messageDialog.close();
});


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



var articleDeletionRequestTask = {
  article: null,
  dialog: new YuleDialog('#confirmDeletion'),
  get jqDialog() {
    return this.dialog.jqDialog;
  },

  init: function(jqDeleteButton) {
    this.article = new ArticleIdData(jqDeleteButton);
    this.dialog.showModal('Are you sure you want to remove' +
        ' the article "' + this.article.title + '"?');
  },

  execute: function() {
    if (this.article === null){
      var error = 'The task wasn\'t initialized!';
      console.log(error);
      throw error;
    }
    messageDialog.showModal('Deleting the article: "' +
        this.article.title + '"...', true);
    var csrfToken = $("meta[name='_csrf']").attr("content");
    if (!csrfToken){
      csrfToken = $('input[name="_csrf"]').val();
    }
    console.log('Sending request for deleting ' + this.article);
    $('<form action=/admin/article/' + this.article.id +
        '/delete method="POST" style="display=none;">' +
        '<input type="hidden" name="_csrf" value="' +
        csrfToken + '"></form>').appendTo('body').submit();
  },

  cancel: function() {
    console.log('Cancelling deletion request task for ' + this.article);
    this.article = null;
  }
};


// .deleteArticle click handler (shows delete dialog)
$('.deleteArticle').each(function(){
  $(this).click(function(){
    articleDeletionRequestTask.init($(this));
  });
});


// #deleteArticleDialog submit handler
articleDeletionRequestTask.jqDialog.on('close', function(event, ui) {
  console.log('Closing the deletion confirmation dialog');
  if (this.returnValue == 'yes') {
    console.log('Delete operation has been confirmed');
    articleDeletionRequestTask.execute();
  } else {
    console.log('Delete operation has been cancelled');
    articleDeletionRequestTask.cancel();
  }
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


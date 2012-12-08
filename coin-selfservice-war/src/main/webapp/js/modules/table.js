var app = app || {};

app.table = function() {

  var ranResponsiveTables = false;

  var init = function() {
    initDataTable();
    responsiveDataTable();
    tableSearchPlaceholder();
    $(window).on('resize', responsiveDataTable);
  };

  var trimmer = function(str) {
    return $.trim(str.replace(/(<([^>]+)>)/ig, ''));
  };

  var initDataTable = function() {
    jQuery.fn.dataTableExt.oSort['spnames-asc'] = function(x, y) {
      x = trimmer(x);
      y = trimmer(y);
      return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['spnames-desc'] = function(x, y) {
      x = trimmer(x);
      y = trimmer(y);
      return ((x < y) ? 1 : ((x > y) ? -1 : 0));
    };

    $('.table-sortable:not(#csp-statusses)').each(function(index, table) {
      $(table).dataTable({
        bPaginate : false,
        bLengthChange : false,
        bAutoWidth : false,
        bInfo : false,
        oLanguage : {
          sSearch : '_INPUT_'
        }
      });
    });

    $('#csp-statusses').dataTable({
      bPaginate : false,
      bLengthChange : false,
      bAutoWidth : false,
      bInfo : false,
      oLanguage : {
        sSearch : '_INPUT_'
      },
      aoColumns : [ {
        'sType' : 'spnames'
      }, null, null, null, null, null ]
    });
  };

  var tableSearchPlaceholder = function() {
    $('.dataTables_filter input[aria-controls]').each(function(index) {
      var elem = $(this);
      if (!elem.attr('placeholder')) {
        var placeholder = elem.closest('.dataTables_wrapper').find('table').data('search-placeholder');
        if (placeholder) {
          elem.attr('placeholder', placeholder);
        } else {
          elem.attr('placeholder', app.message.i18n('jsp.search_placeholder'));
        }
      }
    });
  };

  var responsiveDataTable = function() {
    var tables = $('.data-table-wrapper table');

    if (tables.length) {
      if ($(window).width() < 767) {
        tables.addClass('narrow');
      } else {
        tables.removeClass('narrow');
      }

      if (!ranResponsiveTables) {
        ranResponsiveTables = true;

        tables.each(function() {
          var table = $(this), headers = [], tr;

          table.find('thead th').each(function() {
            headers.push($(this).text());
          });

          table.find('tr').each(function() {
            tr = $(this);

            tr.find('td').each(function(index, td) {
              td = $(td);

              td.attr('data-title', headers[index]);
            });
          });
        });
      }
    }
  };

  return {
    init : init
  };
}();

app.register(app.table);

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


  var dateFromNlDate = function(nlDate) {
    var dateFormat = /(\d{2})-(\d{2})-(\d{4})/;
    var fields = dateFormat.exec(nlDate);
    if (!fields) {
      return null;
    }
    return new Date(
      (+fields[3]),
      (+fields[2])-1, // Careful, month starts at 0!
      (+fields[1]));
  };

  var initDataTable = function() {
    jQuery.fn.dataTableExt.oSort['spnames-asc'] = function(x, y) {
      x = trimmer(x).toLowerCase();
      y = trimmer(y).toLowerCase();
      return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['spnames-desc'] = function(x, y) {
      x = trimmer(x).toLowerCase();
      y = trimmer(y).toLowerCase();
      return ((x < y) ? 1 : ((x > y) ? -1 : 0));
    };

    jQuery.fn.dataTableExt.oSort['nlDate-asc'] = function(x, y) {
      var xDate = dateFromNlDate(x);
      var yDate = dateFromNlDate(y);

      return ((xDate < yDate) ? -1 : ((xDate > yDate) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['nlDate-desc'] = function(x, y) {
      var xDate = dateFromNlDate(x);
      var yDate = dateFromNlDate(y);
      return ((xDate < yDate) ? 1 : ((xDate > yDate) ? -1 : 0));
    };

    jQuery.fn.dataTableExt.oSort['boolean-asc'] = function(x, y) {
      return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['boolean-desc'] = function(x, y) {
      return ((x < y) ? 1 : ((x > y) ? -1 : 0));
    };

    $('.table-sortable:not(#csp-statusses, #license_contact_persons_overview_table, #sp_overview_table, #csp-statusses-short, #request-overview-table, #notifications-overview-table)').each(
        function(index, table) {
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

    $('#notifications-overview-table').dataTable({
      bPaginate : false,
      bLengthChange : false,
      bAutoWidth : false,
      bInfo : false,
      oLanguage : {
        sSearch : '_INPUT_'
      },
      aoColumns : [
        {'bSortable': false},
        { // title
          'sType' : 'spnames'
        },
        null,
        null
      ],
      aaSorting: [[1,'asc']]
    });

    $('#request-overview-table').dataTable({
      bPaginate : false,
      bLengthChange : false,
      bAutoWidth : false,
      bInfo : false,
      oLanguage : {
        sSearch : '_INPUT_'
      },
      aoColumns : [
        {'sType': 'nlDate'},
        { // title
          'sType' : 'spnames'
        },
        null,
        null
      ],
      aaSorting: [[0,'desc']] // sort on date desc
    });

    $('#sp_overview_table').dataTable({
      bPaginate : false,
      bLengthChange : false,
      bAutoWidth : false,
      bInfo : false,
      oLanguage : {
        sSearch : '_INPUT_'
      },
      aoColumns : [
        {'sType' : 'spnames'},
        {'sType' : 'boolean'},
        {'sType' : 'boolean'},
        {'bSortable': false},
        {'bSortable': false},
        {'bSortable': false},
        {'bSortable': false},
        {'bSortable': false},
        {'bSortable': false} ]
    });

    $('#license_contact_persons_overview_table').dataTable({
      bPaginate : false,
      bLengthChange : false,
      bAutoWidth : false,
      bInfo : false,
      oLanguage : {
        sSearch : '_INPUT_'
      },
      aoColumns : [
        {'sType' : 'spnames'},
        {'sType' : 'spnames'},
        {'sType' : 'spnames'},
        {'sType' : 'spnames'}]

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
      }, null, null, null, null, null, null ]
    });

    $('#csp-statusses-short').dataTable({
      bPaginate : false,
      bLengthChange : false,
      bAutoWidth : false,
      bInfo : false,
      oLanguage : {
        sSearch : '_INPUT_'
      },
      aoColumns : [ {
        'sType' : 'spnames'
      }, null ]
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

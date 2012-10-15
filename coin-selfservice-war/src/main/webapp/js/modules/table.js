var app = app || {};

app.table = function() {
    var ranResponsiveTables = false;


    var init = function() {
        initDataTable();
        responsiveDataTable();

        $(window).on('resize', responsiveDataTable);
    };


    var initDataTable = function() {
        $('.table-sortable').each(function(index, table) {
            $(table).dataTable({
                bPaginate: false,
                bLengthChange: false,
                bAutoWidth: false,
                bInfo: false,
                oLanguage: {
                    sSearch: '_INPUT_'
                }
            });
        });
    };


    var responsiveDataTable = function() {
        var tables = $('.data-table-wrapper table');

        if (tables.length) {
            if ($(window).width() < 767) {
                tables.addClass('narrow');
            }
            else {
                tables.removeClass('narrow');
            }

            if (!ranResponsiveTables) {
                ranResponsiveTables = true;

                tables.each(function() {
                    var table = $(this),
                        headers = [],
                        tr;

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
        init: init
    };
}();

app.register(app.table);

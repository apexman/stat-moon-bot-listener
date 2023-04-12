var lastTradeTime = +new Date();
$(document).ready(function() {
    var stompUri = '/local-websocket'
    var socket = new SockJS(stompUri);
    var client = Stomp.over(socket);
    var maxItems = 25;
    var cnt = 0;
    var sum = 0;
    var cntStat = 0;

    var trades = $('#out_trades').DataTable({
        responsive: true,
        scrollY: false,
        "initComplete": function(settings, json) {
            this.DataTable().draw();
        },
        "columnDefs": [
            { "orderData": [0],"targets": 3 },
            { "width": "5%", "targets": 0 },
            { "width": "20%", "targets": 1 },
            { "width": "5%", "targets": 2 },
            { "width": "5%", "targets": 3 },
            { "width": "15%", "targets": 4 },
            { "width": "15%", "targets": 5 },
            { "width": "15%", "targets": 6 },
        ],
        processing: true,
        dom: 'Bfrtip',
        "order": [[ 3, "desc" ]],
        paging: false,
        searching: false,
        lengthChange: true,
        scrollY: '75vh',
        stateSave: true,
        info: true,
        drawCallback: function( settings ) {
            $(".uk-table").css({"width":"100% !important"});
        },
        rowCallback: function(row, data, index) {
            $(row).find('td:eq(3)').css('color', '#6c7502');
            if(data[8]>= 0) {
                $(row).find('td:eq(7)').css('color', '#69D592');
                $(row).find('td:eq(8)').css('color', '#69D592');
            }
            else {
                $(row).find('td:eq(7)').css('color', '#E64C72');
                $(row).find('td:eq(8)').css('color', '#E64C72');
            }
        },
    });

    window.onbeforeunload = function (evt) {
            if (client != null) client.disconnect();
    };

    var column = trades.column(0);
    column.visible(false);
    trades.search( '' ).columns().search( '' ).draw();

    var r=[];

    function successCallback() {
        client.subscribe("/data/trades", function (msg) {
            try {
                lastTradeTime = +new Date();
                var trades = JSON.parse(msg.body);
                var i = 0;
                for (i; i < trades.length; i += 1) {
                    r = trades[i];
                    addRow(r.i, r.bi, r.t, tc(r.l), r.c, r.b, r.s, r.o, r.u, r.p, r.a, r.f);
                }
            } catch (err) {
            }
        });
    }

    function reconnect(socketUrl, successCallback) {
        let connected = false;
        let reconInv = setInterval(() => {
            socket = new SockJS(socketUrl);
            client = Stomp.over(socket);
            client.connect({}, (frame) => {
                clearInterval(reconInv);
                connected = true;
                successCallback();
            }, () => {
                if (connected) {
                    reconnect(socketUrl, successCallback);
                }
            });
        }, 10000);
    }

    client.connect({}, (frame) => {
        successCallback();
    }, () => {
        reconnect(stompUri, successCallback);
    });

    function addRow(i, bi, t, l, c, b, s, o, u, p, a, f) {
        try {
            removeRows(0);
        } catch(err) {
        }
        var type = 'free';
        if (f == 1) type = 'pro';

        var trade = 'manual';
        if (a > 0) trade = 'auto';
        if (a == 13) trade = 'manual';

        var rowNode = trades
            .row.add([i, bi, t, l, c, b, s, o, u, p, trade, type])
            .draw(false)
            .node();

        $(rowNode)
            .css({color: '#1c0707'})
            .animate({color: '#2c2a2a', width:3}, 300)
            .animate({color: '#1c0707', width:3}, 300);
    }

    function removeRows(i) {
        var len = trades.page.info().end;
        if (len > maxItems-1) {
            var m = 0;
            for (m; m < len - maxItems + i; m += 1) {
                var row = trades.row(0);
                row.remove().draw();
            }
        }
    }

    function tc(UNIX_timestamp) {
        var a = new Date(UNIX_timestamp * 1000);
        var hour = a.getHours();
        var min = a.getMinutes();
        var sec = a.getSeconds();
        var time = hour + ':' + min + ':' + sec ;
        return time;
    }

    function disconnect() {
        if (client!=null) client.disconnect();
    };
});
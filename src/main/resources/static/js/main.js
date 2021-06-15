/**
 * add counter to first column of DataTable
 * @param table
 */
function addCounterToDataTable(table) {
    table.on('order.dt search.dt', function () {
        table.column(0, {search: 'applied', order: 'applied'}).nodes().each(function (cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();
}

/**
 * show(hide) <a></a> buttons on row selection(deselection) and handle buttons click
 * @param table
 * @param functions
 */
function addDataTableRowListener(table, functions) {
    table
        .on('select', function ( e, dt, type, indexes ) {
            if (type === 'row') {
                var nodes$ = dt.rows(indexes).nodes().to$();
                var a = nodes$.find("a");
                if (a !== undefined) {
                    a.removeClass("invisible");
                }
                if (functions !== undefined && functions['select'] !== undefined) {
                    functions['select'](nodes$.closest('tr'), table);
                }
            }
        })
        .on('deselect',  function ( e, dt, type, indexes ) {
            if (type === 'row') {
                var nodes$ = dt.rows(indexes).nodes().to$();
                var a = nodes$.find("a");
                if (a !== undefined) {
                    a.addClass("invisible");
                }
                if (functions !== undefined && functions['deselect'] !== undefined) {
                    functions['deselect'](nodes$.closest('tr'), table);
                }
            }
        });

    $('#' + table.tables().nodes().to$().attr('id') + ' tbody').on('click', 'a', function(e) {
        e.stopPropagation();

        var f = $(this).attr("id");
        if (f !== undefined && functions !== undefined && functions[f] !== undefined) {
            functions[f]($(this).closest('tr'), table);
        }
    });
}

/**
 * add button listeners for modal window
 * @param modal
 * @param functions
 */
function addModalButtonsListener(modal, functions) {
    var buttonId;

    $(modal).on('click', 'button', function(e) {
        buttonId = $(e.target).attr("id");
        $(modal).modal('hide');
    });

    $(modal).on('hidden.bs.modal', function (e) {
        if (buttonId !== undefined && functions !== undefined && functions[buttonId] !== undefined) {
            functions[buttonId]();
        }
    });
}

/**
 * show ok modal diallog
 * @param message
 * @param ok - function
 */
function showInfoModal(message, ok) {
    showDynamicModal( {
        //header: "Внимание!",
        body : message,
        buttons : [
            {
                id : "ok",
                class : "btn btn-success",
                text : "ОК",
                action: function () {
                    if (ok !== undefined) {
                        ok();
                    }
                }
            }
        ]
    });
}

/**
 * show yes/no modal diallog
 * @param message
 * @param yes yes event function(param)
 * @param no no event function(param)
 */
function showYesNoModal(message, yes, no) {
    showDynamicModal( {
        header: "Внимание",
        body : message,
        buttons : [
            {
                id : "yes",
                class : "btn btn-success",
                text : "Да",
                action : function() {
                    if (yes !== undefined) {
                        yes();
                    }
                }
            },
            {
                id : "no",
                class : "btn btn-danger",
                text : "Нет",
                action : function() {
                    if (no !== undefined) {
                        no();
                    }
                }
            }
        ]
    });
}

/**
 * show dynamically created modal
 * @param p
 */
function showDynamicModal(p) {

    var modalHtml =
        "    <div id=\"dynamicModal\" class=\"modal\" tabindex=\"-1\">\n" +
        "        <div class=\"modal-dialog\">\n" +
        "            <div class=\"modal-content\">\n";

    if (p.header !== undefined) {
        modalHtml +=
            "                <div class=\"modal-header\">\n" +
            "                    <h5 class=\"modal-title\">" + p.header + "</h5>\n" +
            "<!--                    <button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button>-->\n" +
            "                </div>\n";
    }

    modalHtml +=
        "                <div class=\"modal-body\">\n" +
        "                    " + p.body + "\n" +
        "                </div>\n" +
        "                <div class=\"modal-footer\">\n";

    for (var i = 0; i < p.buttons.length; i++) {
        var b = p.buttons[i];
        modalHtml +=
            "                    <button id=\"" + b.id + "\" type=\"button\" class=\"" + b.class + "\">" + b.text + "</button>\n";
    }

    modalHtml +=
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>";

    $('body').append(modalHtml);

    var modal = $("#dynamicModal");

    var buttonId;

    modal.on('click', 'button', function(e) {
        buttonId = $(e.target).attr("id");
        modal.modal('hide');
    });

    modal.on('hidden.bs.modal', function (e) {
        for (var i = 0; i < p.buttons.length; i++) {
            var b = p.buttons[i];
            if (buttonId === b.id) {
                try {
                    b.action();
                } catch (e) {
                    console.log(e);
                }
            }
        }
        $(this).remove();
    });

    modal.modal('show');
}

/**
 * create modal with button listeners
 * @param modal
 * @param functions
 * @return m
 */

function initModal(modal, functions) {
    var buttonId, params;

    var _modal = $(modal);

    _modal.on('click', 'button', function(e) {
        if ($(e.target).data("hide") !== 'no') {
            buttonId = $(e.target).attr("id");
            $(modal).modal('hide');
        }
    });

    _modal.on('hidden.bs.modal', function (e) {
        if (buttonId !== undefined && functions !== undefined && functions[buttonId] !== undefined) {
            functions[buttonId](params, _modal);
        }
        if (functions !== undefined && functions['onclose'] !== undefined) {
            functions['onclose'](params, _modal);
        }
    });

    var m = {};
    m.show = function(p, f) {
        buttonId = undefined;
        params = p;
        if (functions === undefined && f !== undefined) {
            functions = f;
        }
        if (f !== undefined && f["before"] !== undefined) {
            f["before"](params, _modal);
        }
        _modal.modal('show');
        if (f !== undefined && f["after"] !== undefined) {
            f["after"](params, _modal);
        }
    };
    m.close = function() {
        _modal.modal('hide');
    };

    return m;
}

/**
 * create modal with button listeners
 * @param modal
 * @param functions
 * @return m
 */

function initModal2(modal, functions) {
    var buttonId, params;

    var _modal = $(modal);

    _modal.on('click', 'button', function(e) {
        buttonId = $(e.target).attr("id");
        if (buttonId !== undefined && functions !== undefined && functions[buttonId] !== undefined) {
            functions[buttonId](params, _modal);
        }
        if ($(e.target).data("hide") !== 'no') {
            _modal.modal('hide');
        }
    });

    _modal.on('hidden.bs.modal', function (e) {
        if (functions !== undefined && functions['onclose'] !== undefined) {
            functions['onclose'](params, _modal);
        }
    });

    var m = {};
    m.show = function(p, f) {
        buttonId = undefined;
        params = p;
        if (functions === undefined && f !== undefined) {
            functions = f;
        }
        if (f !== undefined && f["before"] !== undefined) {
            f["before"](params, _modal);
        }
        _modal.modal('show');
        if (f !== undefined && f["after"] !== undefined) {
            f["after"](params, _modal);
        }
    };
    m.close = function() {
        _modal.modal('hide');
    };

    return m;
}

function upload(params) {
    params.e.preventDefault();

    var button = params.button;
    var filename = params.filename;
    var message = params.message;
    var progressBar = params.progressBar;

    //Disable submit button
    $(button).prop("disabled", true);

    if ($(filename).val() === "") {
        $(button).prop("disabled", false);
        $(message).text("No file");
        $(params.pollParams.text).text("");
        return;
    }

    var formData = new FormData($(params.e.target)[0]);
    formData.append("type", params.type);

    // Ajax call for file uploading
    var request = $.ajax({
        url: contextRoot + "api/upload",
        type: "POST",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        xhr: function () {
            //Get XmlHttpRequest object
            var xhr = $.ajaxSettings.xhr();

            //Set onprogress event handler
            xhr.upload.onprogress = function (event) {
                var perc = Math.round((event.loaded / event.total) * 100);
                $(progressBar).text(perc + "%");
                $(progressBar).css("width", perc + "%");
            };
            return xhr;
        },

        beforeSend: function (xhr) {
            //Reset alert message and progress bar
            $(message).text("");
            $(progressBar).text("");
            $(progressBar).css("width", "0%");
        },

        success: function (msg) {
            $(message).text("Файл " + msg.fileName + " успешно загружен");
            $(filename).val("");

            $(button).prop("disabled", false);

            if (typeof params.pollParams !== "undefined") {
                params.pollParams.clear_text = true;
                start(params.pollParams);
            }

        },

        error: function (jqXHR) {
            $(progressBar).text("");
            $(progressBar).css("width", "0%");
            $(message).text(jqXHR.responseText + "(" + jqXHR.status +
                " - " + jqXHR.statusText + ")");
            $(button).prop("disabled", false);
        }
    });

}

function start(params) {
    var pollParams = params;

    $(pollParams.button).prop("disabled", true);

    if (pollParams.clear_text) {
        $(pollParams.text).text("");
        $(pollParams.text).scrollTop($(pollParams.text)[0].scrollHeight);
    }

    $(pollParams.progressBar).text("");
    $(pollParams.progressBar).css("width", "0%");

    if (request) {
        request.abort(); // abort any pending request
    }

    // fire off the request to MatchUpdateController
    var request = $.ajax({
        url: contextRoot + "api/jobstart",
        data: { job: pollParams.job },
        type: "get",
        cache: false
    });

    // This is jQuery 1.8+
    // callback handler that will be called on success
    request.done(function (reply) {
        $(pollParams.button).prop("disabled", false);
/*
        if (reply.running) {
            poll(pollParams);
        }
*/
    });

    // callback handler that will be called on failure
    request.fail(function (jqXHR, textStatus, errorThrown) {
        // log the error to the console
        //console.log("Start - the following error occured: " + textStatus, errorThrown);
        $(pollParams.button).prop("disabled", false);
    });

    //$(params.text).text("finished");
}


function poll(pollParams) {
    $(pollParams.button).prop("disabled", true);
    (function _poll(){
        $.ajax({
            url: contextRoot + "api/jobstatus",
            data: {
                job: pollParams.job,
                jobStep: typeof pollParams.jobStep === "undefined" ? null : pollParams.jobStep
            },
            type: "get",
            cache: false,
            success: function(message){
                if (!message.timeout) {
                    $(pollParams.text).text(message.message);
                    $(pollParams.text).scrollTop($(pollParams.text)[0].scrollHeight);

                    $(pollParams.progressBar).text(message.progress + "%");
                    $(pollParams.progressBar).css("width", message.progress + "%");
                }
                pollParams.jobStep = message.jobStep;
                $(pollParams.button).prop("disabled", message.running);

                if (!message.abort) {
                    _poll();
                }
            },
            error: function() {
                $(pollParams.button).prop("disabled", false);
            },
            dataType: "json"});
    })();

}

var nsiMap = new Map();

/**
 * get cached NSI for TpolItem
 * @param item
 * @param callback
 */
function getNsi(item, callback) {
    var key = item.name + ":" + item.set;
    var nsiData = nsiMap.get(key);

    var data = {};

    if (item.set !== 0) {
        data.set = item.set;
    }

    if (nsiData === undefined) {
        $.ajax({
            url: contextRoot + "api/tpol/items/nsi/" + item.name,
            type: "get",
            data: data,
            cache: false,
            success: function (data) {
                nsiMap.set(key, nsiData = data);
                console.log("load data for " + key);
                callback(nsiData);
            }
        });
    } else {
        console.log("use cached data for " + key);
        callback(nsiData);
    }

}

/**
 * scrollIntoViewIfNeeded support for IE and Firefox
 * https://stackoverflow.com/a/34003331
 */
if (!Element.prototype.scrollIntoViewIfNeeded) {
    Element.prototype.scrollIntoViewIfNeeded = function (centerIfNeeded) {
        "use strict";

        function makeRange(start, length) {
            return {"start": start, "length": length, "end": start + length};
        }

        function coverRange(inner, outer) {
            if (false === centerIfNeeded ||
                (outer.start < inner.end && inner.start < outer.end))
            {
                return Math.min(
                    inner.start, Math.max(outer.start, inner.end - outer.length)
                );
            }
            return (inner.start + inner.end - outer.length) / 2;
        }

        function makePoint(x, y) {
            return {
                "x": x, "y": y,
                "translate": function translate(dX, dY) {
                    return makePoint(x + dX, y + dY);
                }
            };
        }

        function absolute(elem, pt) {
            while (elem) {
                pt = pt.translate(elem.offsetLeft, elem.offsetTop);
                elem = elem.offsetParent;
            }
            return pt;
        }

        var target = absolute(this, makePoint(0, 0)),
            extent = makePoint(this.offsetWidth, this.offsetHeight),
            elem = this.parentNode,
            origin;

        while (elem instanceof HTMLElement) {
            // Apply desired scroll amount.
            origin = absolute(elem, makePoint(elem.clientLeft, elem.clientTop));
            elem.scrollLeft = coverRange(
                makeRange(target.x - origin.x, extent.x),
                makeRange(elem.scrollLeft, elem.clientWidth)
            );
            elem.scrollTop = coverRange(
                makeRange(target.y - origin.y, extent.y),
                makeRange(elem.scrollTop, elem.clientHeight)
            );

            // Determine actual scroll amount by reading back scroll properties.
            target = target.translate(-elem.scrollLeft, -elem.scrollTop);
            elem = elem.parentNode;
        }
    };
}

Date.prototype.getStringDate = function(format) {
    var mm = this.getMonth() + 1; // getMonth() is zero-based
    var dd = this.getDate();

    return [
        (dd > 9 ? '' : '0') + dd,
        (mm > 9 ? '' : '0') + mm,
        this.getFullYear()
    ].join('.');
};

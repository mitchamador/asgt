function calc(params) {
    params.e.preventDefault();

    var text = params.text;
    var button = params.button;
    var message = params.message;

    if ($(button).prop("disabled")) {
        return;
    }

    $(button).button('loading');

    //Disable submit button
    $(button).prop("disabled", true);

    $.ajax({
        url: contextRoot + "api/calc",
        type: "POST",
        data: { data : $(text).val()},
        cache: false,
        success: function (msg) {
            $(message).text(msg.message);
            $(button).prop("disabled", false);
            $(button).button('reset');
        },
        error: function(jqXHR) {
            $(button).prop("disabled", false);
            $(button).button('reset');
        }
    });
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

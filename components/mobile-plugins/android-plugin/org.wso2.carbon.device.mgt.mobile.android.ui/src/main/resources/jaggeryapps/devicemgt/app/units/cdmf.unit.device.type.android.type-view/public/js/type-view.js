/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var modalPopup = ".modal",
    modalPopupContainer = modalPopup + " .modal-content",
    modalPopupContent = modalPopup + " .modal-content";

var backendEndBasePath = "/api/device-mgt/v1.0";

//function openCollapsedNav() {
//    $(".wr-hidden-nav-toggle-btn").addClass("active");
//    $("#hiddenNav").slideToggle("slideDown", function () {
//        if ($(this).css("display") == "none") {
//            $(".wr-hidden-nav-toggle-btn").removeClass("active");
//        }
//    });
//}

var kioskConfigs = {
    "adminComponentName": "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME",
    "wifiSSID": "android.app.extra.PROVISIONING_WIFI_SSID",
    "wifiPassword": "android.app.extra.PROVISIONING_WIFI_PASSWORD",
    "wifiSecurity": "android.app.extra.PROVISIONING_WIFI_SECURITY_TYPE",
    "skipEncryption": "android.app.extra.PROVISIONING_SKIP_ENCRYPTION",
    "checksum": "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM",
    "downloadURL": "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION",
    "androidExtra": "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE",
    "accessToken": "android.app.extra.token",
    "defaultOwnership": "android.app.extra.defaultOwner",
    "serverIp": "android.app.extra.serverIp"
};

/*
 * set popup maximum height function.
 */
function setPopupMaxHeight() {
    var maxHeight = "max-height";
    var marginTop = "margin-top";
    var body = "body";
    $(modalPopupContent).css(maxHeight, ($(body).height() - ($(body).height() / 100 * 30)));
    $(modalPopupContainer).css(marginTop, (-($(modalPopupContainer).height() / 2)));
}

/*
 * show popup function.
 */
function showPopup() {
    $(modalPopup).modal('show');
}

/*
 * hide popup function.
 */
function hidePopup() {
    $(modalPopupContent).html("");
    $(modalPopupContent).removeClass("operation-data");
    $(modalPopup).modal('hide');
    $('body').removeClass('modal-open').css('padding-right', '0px');
    $('.modal-backdrop').remove();
}

/*
 * QR-code generation function.
 */
function generateQRCode() {
    var enrollmentURL = $("#qr-code-modal").data("enrollment-url");
    $(qrCodeClass).qrcode({
        text: enrollmentURL,
        width: 150,
        height: 150
    });
}

/*
 * QR-code generation function for KIOSK.
 */
function generateKIOSKQRCode(qrCodeClass) {

    var payload = {};
    var androidConfigAPI = "/api/device-mgt/android/v1.0/configuration";

    var isKioskConfigured = false;
    var defaultOwnerVal = {};
    var serverIp = {};
    $("#android-qr-code").show();
    $("#enrollment_qr_content").show();
    $("#qr-code-img").hide();
    $("#enroll-qr-heading").show();
    var ownership_type = $("#android-device-ownership").find("option:selected").attr("value");

    invokerUtil.get(
        androidConfigAPI,
        function(data) {
            data = JSON.parse(data);
            if (data != null && data.configuration != null) {
                if (ownership_type == "COSU" || ownership_type == "COPE") {
                    for (var i = 0; i < data.configuration.length; i++) {
                        var config = data.configuration[i];
                        if (config.name === kioskConfigs["adminComponentName"]) {
                            isKioskConfigured = true;
                            payload[config.name] = config.value;
                        } else if (config.name === kioskConfigs["wifiSSID"]) {
                            payload[config.name] = config.value;
                        } else if (config.name === kioskConfigs["wifiPassword"]) {
                            payload[config.name] = config.value;
                        } else if (config.name === kioskConfigs["wifiSecurity"]) {
                            payload[config.name] = config.value;
                        } else if (config.name === kioskConfigs["checksum"]) {
                            payload[config.name] = config.value;
                        } else if (config.name === kioskConfigs["downloadURL"]) {
                            payload[config.name] = config.value;
                        } else if (config.name === kioskConfigs["skipEncryption"]) {
                            payload[config.name] = Boolean(config.value);
                        } else if (config.name === kioskConfigs["defaultOwnership"]) {
                            defaultOwnerVal[config.name] = ownership_type;
                        } else if (config.name === kioskConfigs["serverIp"]) {
                            serverIp[config.name] = config.value;
                        }
                    }
                } else {
                    for (var i = 0; i < data.configuration.length; i++) {
                        var config = data.configuration[i];
                        if (config.name === kioskConfigs["adminComponentName"]) {
                            isKioskConfigured = true;
                            payload[config.name] = config.value;
                        } else if (config.name === kioskConfigs["defaultOwnership"]) {
                            defaultOwnerVal[config.name] = ownership_type;
                        } else if (config.name === kioskConfigs["serverIp"]) {
                            serverIp[config.name] = config.value;
                        }
                    }
                }
            }
        },
        function(data) {
            console.log(data);
        });

    var aToken = $(".a-token");
    var tokenPair = aToken.data("atoken");

    var accessToken = {};
    accessToken[kioskConfigs["accessToken"]] = tokenPair["accessToken"];
    var sumExtra = $.extend(accessToken, defaultOwnerVal, serverIp);
    payload[kioskConfigs["androidExtra"]] = sumExtra;
    $(".kiosk-enrollment-qr-container").empty();
    if (isKioskConfigured) {
        $(qrCodeClass).qrcode({
            text: JSON.stringify(payload),
            width: 350,
            height: 350
        });
    } else {
        $("#android-configurations-alert").fadeToggle(500);
        $("#kiosk_content").hide();
    }
}

function displayAgentDownloadQR() {
    $(".enrollment-qr-container").empty();
    generateQRCode(".enrollment-qr-container");
    $("#download-agent-qr").fadeToggle(1000);
    $("#enroll-agent-qr").fadeOut(1000);
    $("#android-enroll-manually-instructions").fadeOut(1000);
    $('html,body').animate({
        scrollTop: $('#download-agent-qr').position().top
    }, 800);
}

function displayEnrollmentQR() {
    $("#download-agent-qr").fadeOut(1000);
    $("#enroll-agent-qr").fadeToggle(1000);
    $("#android-enroll-manually-instructions").fadeOut(1000);
    $('html,body').animate({
        scrollTop: $('#enroll-agent-qr').position().top
    }, 800);
}

function manualEnrollmentGuide() {
    $("#enroll-agent-qr").fadeOut(1000);
    $("#download-agent-qr").fadeOut(1000);
    $("#android-enroll-manually-instructions").fadeToggle(1000);
    $('html,body').animate({
        scrollTop: $('#android-enroll-manually-instructions').position().top
    }, 800);
}

function toggleEnrollment() {
    $(".modal-content").html($("#qr-code-modal").html());
    generateQRCode(".modal-content .qr-code");
    modalDialog.show();
}

var updateNotificationCountOnSuccess = function(data, textStatus, jqXHR) {
    var notificationBubble = "#notification-bubble";
    if (jqXHR.status == 200 && data) {
        var responsePayload = JSON.parse(data);
        var newNotificationsCount = responsePayload["count"];
        if (newNotificationsCount > 0) {
            $(notificationBubble).html(newNotificationsCount);
            $(notificationBubble).show();
        } else {
            $(notificationBubble).hide();
        }
    }
};

function updateNotificationCountOnError() {
    var notificationBubble = "#notification-bubble";
    $(notificationBubble).html("Error");
    $(notificationBubble).show();
}

function loadNewNotificationsOnSideViewPanel() {
    if ($("#right-sidebar").attr("is-authorized") == "false") {
        $("#notification-bubble-wrapper").remove();
    } else {
        var serviceURL = backendEndBasePath + "/notifications?status=NEW";
        invokerUtil.get(serviceURL, updateNotificationCountOnSuccess, updateNotificationCountOnError);
        loadNewNotifications();
    }
}

function loadNewNotifications() {
    var messageSideBar = ".sidebar-messages";
    if ($("#right-sidebar").attr("is-authorized") == "false") {
        $(messageSideBar).html("<h4 class ='message-danger'>You are not authorized to view notifications.</h4>");
    } else {
        var notifications = $("#notifications");
        var currentUser = notifications.data("currentUser");

        $.template("notification-listing", notifications.attr("src"), function(template) {
            var serviceURL = backendEndBasePath + "/notifications?offset=0&limit=5&status=NEW";
            invokerUtil.get(
                serviceURL,
                // on success
                function(data, textStatus, jqXHR) {
                    if (jqXHR.status == 200 && data) {
                        var viewModel = {};
                        var responsePayload = JSON.parse(data);
                        if (responsePayload["notifications"]) {
                            if (responsePayload.count > 0) {
                                viewModel["notifications"] = responsePayload["notifications"];
                                viewModel["appContext"] = context;
                                $(messageSideBar).html(template(viewModel));
                            } else {
                                $(messageSideBar).html("<h4 class='text-center'>No New Notifications</h4>" +
                                    "<h5 class='text-center text-muted'>" +
                                    "Check this section for error notifications<br>related to device operations" +
                                    "</h5>");
                            }
                        } else {
                            $(messageSideBar).html("<h4 class ='message-danger'>Unexpected error " +
                                "occurred while loading new notifications.</h4>");
                        }
                    }
                },
                // on error
                function(jqXHR) {
                    if (jqXHR.status = 500) {
                        $(messageSideBar).html("<h4 class ='message-danger'>Unexpected error occurred while trying " +
                            "to retrieve any new notifications.</h4>");
                    }
                }
            );
        });
    }
}

/**
 * Toggle function for
 * notification listing sidebar.
 * @return {Null}
 */
$.sidebar_toggle = function(action, target, container) {
    var elem = '[data-toggle=sidebar]',
        button,
        containerOffsetLeft,
        containerOffsetRight,
        targetOffsetLeft,
        targetOffsetRight,
        targetWidth,
        targetSide,
        relationship,
        pushType,
        buttonParent;

    var sidebar_window = {
        update: function(target, container, button) {
            containerOffsetLeft = $(container).data('offset-left') ? $(container).data('offset-left') : 0;
            containerOffsetRight = $(container).data('offset-right') ? $(container).data('offset-right') : 0;
            targetOffsetLeft = $(target).data('offset-left') ? $(target).data('offset-left') : 0;
            targetOffsetRight = $(target).data('offset-right') ? $(target).data('offset-right') : 0;
            targetWidth = $(target).data('width');
            targetSide = $(target).data("side");
            pushType = $(container).parent().is('body') == true ? 'padding' : 'margin';

            if (button !== undefined) {
                relationship = button.attr('rel') ? button.attr('rel') : '';
                buttonParent = $(button).parent();
            }
        },

        show: function() {
            if ($(target).data('sidebar-fixed') == true) {
                $(target).height($(window).height() - $(target).data('fixed-offset'));
            }
            $(target).trigger('show.sidebar');
            if (targetWidth !== undefined) {
                $(target).css('width', targetWidth);
            }
            $(target).addClass('toggled');
            if (button !== undefined) {
                if (relationship !== '') {
                    // Removing active class from all relative buttons
                    $(elem + '[rel=' + relationship + ']:not([data-handle=close])').removeClass("active");
                    $(elem + '[rel=' + relationship + ']:not([data-handle=close])').attr('aria-expanded', 'false');
                }
                // Adding active class to button
                if (button.attr('data-handle') !== 'close') {
                    button.addClass("active");
                    button.attr('aria-expanded', 'true');
                }
                if (buttonParent.is('li')) {
                    if (relationship !== '') {
                        $(elem + '[rel=' + relationship + ']:not([data-handle=close])').parent().removeClass("active");
                        $(elem + '[rel=' + relationship + ']:not([data-handle=close])').parent().attr('aria-expanded', 'false');
                    }
                    buttonParent.addClass("active");
                    buttonParent.attr('aria-expanded', 'true');
                }
            }
            // Sidebar open function
            if (targetSide == 'left') {
                if ((button !== undefined) && (button.attr('data-container-divide'))) {
                    $(container).css(pushType + '-' + targetSide, targetWidth + targetOffsetLeft);
                }
                $(target).css(targetSide, targetOffsetLeft);
            } else if (targetSide == 'right') {
                if ((button !== undefined) && (button.attr('data-container-divide'))) {
                    $(container).css(pushType + '-' + targetSide, targetWidth + targetOffsetRight);
                }
                $(target).css(targetSide, targetOffsetRight);
            }
            $(target).trigger('shown.sidebar');
        },

        hide: function() {
            $(target).trigger('hide.sidebar');
            $(target).removeClass('toggled');
            if (button !== undefined) {
                if (relationship !== '') {
                    // Removing active class from all relative buttons
                    $(elem + '[rel=' + relationship + ']:not([data-handle=close])').removeClass("active");
                    $(elem + '[rel=' + relationship + ']:not([data-handle=close])').attr('aria-expanded', 'false');
                }
                // Removing active class from button
                if (button.attr('data-handle') !== 'close') {
                    button.removeClass("active");
                    button.attr('aria-expanded', 'false');
                }
                if ($(button).parent().is('li')) {
                    if (relationship !== '') {
                        $(elem + '[rel=' + relationship + ']:not([data-handle=close])').parent().removeClass("active");
                        $(elem + '[rel=' + relationship + ']:not([data-handle=close])').parent().attr('aria-expanded', 'false');
                    }
                }
            }
            // Sidebar close function
            if (targetSide == 'left') {
                if ((button !== undefined) && (button.attr('data-container-divide'))) {
                    $(container).css(pushType + '-' + targetSide, targetOffsetLeft);
                }
                $(target).css(targetSide, -Math.abs(targetWidth + targetOffsetLeft));
            } else if (targetSide == 'right') {
                if ((button !== undefined) && (button.attr('data-container-divide'))) {
                    $(container).css(pushType + '-' + targetSide, targetOffsetRight);
                }
                $(target).css(targetSide, -Math.abs(targetWidth + targetOffsetRight));
            }
            $(target).trigger('hidden.sidebar');
        }
    };
    if (action === 'show') {
        sidebar_window.update(target, container);
        sidebar_window.show();
    }
    if (action === 'hide') {
        sidebar_window.update(target, container);
        sidebar_window.hide();
    }
    // binding click function
    var body = 'body';
    $(body).off('click', elem);
    $(body).on('click', elem, function(e) {
        e.preventDefault();
        button = $(this);
        container = button.data('container');
        target = button.data('target');
        sidebar_window.update(target, container, button);
        /**
         * Sidebar function on data container divide
         * @return {Null}
         */
        if (button.attr('aria-expanded') == 'false') {
            sidebar_window.show();
        } else if (button.attr('aria-expanded') == 'true') {
            sidebar_window.hide();
        }
    });
};

$.fn.collapse_nav_sub = function() {
    var navSelector = 'ul.nav';

    if (!$(navSelector).hasClass('collapse-nav-sub')) {
        $(navSelector + ' > li', this).each(function() {
            var position = $(this).offset().left - $(this).parent().scrollLeft();
            $(this).attr('data-absolute-position', (position + 5));
        });

        $(navSelector + ' li', this).each(function() {
            if ($('ul', this).length !== 0) {
                $(this).addClass('has-sub');
            }
        });

        $(navSelector + ' > li', this).each(function() {
            $(this).css({
                'left': $(this).data('absolute-position'),
                'position': 'absolute'
            });
        });

        $(navSelector + ' li.has-sub', this).on('click', function() {
            var elem = $(this);
            if (elem.attr('aria-expanded') !== 'true') {
                elem.siblings().fadeOut(100, function() {
                    elem.animate({ 'left': '15' }, 200, function() {
                        $(elem).first().children('ul').fadeIn(200);
                    });
                });
                elem.siblings().attr('aria-expanded', 'false');
                elem.attr('aria-expanded', 'true');
            } else {
                $(elem).first().children('ul').fadeOut(100, function() {
                    elem.animate({ 'left': $(elem).data('absolute-position') }, 200, function() {
                        elem.siblings().fadeIn(100);
                    });
                });
                elem.siblings().attr('aria-expanded', 'false');
                elem.attr('aria-expanded', 'false');
            }
        });

        $(navSelector + ' > li.has-sub ul', this).on('click', function(e) {
            e.stopPropagation();
        });
        $(navSelector).addClass('collapse-nav-sub');
    }
};

$(document).ready(function() {
    $.sidebar_toggle();

    if (typeof $.fn.collapse == 'function') {
        $('.navbar-collapse.tiles').on('shown.bs.collapse', function() {
            $(this).collapse_nav_sub();
        });
    }

    loadNewNotificationsOnSideViewPanel();
    $("#right-sidebar").on("click", ".new-notification", function() {
        var notificationId = $(this).data("id");
        var redirectUrl = $(this).data("url");
        var markAsReadNotificationsEpr = backendEndBasePath + "/notifications/" + notificationId + "/mark-checked";
        var messageSideBar = ".sidebar-messages";

        invokerUtil.put(
            markAsReadNotificationsEpr,
            null,
            // on success
            function(data) {
                data = JSON.parse(data);
                if (data.statusCode == responseCodes["ACCEPTED"]) {
                    location.href = redirectUrl;
                }
            },
            // on error
            function() {
                var content = "<li class='message message-danger'><h4><i class='icon fw fw-error'></i>Warning</h4>" +
                    "<p>Unexpected error occurred while loading notification. Please refresh the pa{{#if isCloud}}ge and" +
                    " try again</p></li>";
                $(messageSideBar).html(content);
            }
        );
    });
});
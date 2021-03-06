(function($, window) {
  var menus = {};
  $.fn.contextMenu = function(settings) {
    var $menu = $(settings.menuSelector);
    $menu.data("menuSelector", settings.menuSelector);
    if ($menu.length === 0) {
      //return;
    }

    menus[settings.menuSelector] = {
      $menu: $menu,
      settings: settings
    };

    //make sure menu closes on any click
    $(document).click(function(e) {
      hideAll();
    });
    $(document).on("contextmenu", function(e) {
      var $ul = $(e.target).closest("ul");
      if ($ul.length === 0 || !$ul.data("menuSelector")) {
        hideAll();
      }
    });

    // Open context menu
    (function(element, settings) {
      var menuSelector = settings.menuSelector
      element.on("contextmenu", function(e) {
        // return native menu if pressing control
        if (e.ctrlKey) return;

        if (settings.disabled) {
          e.preventDefault();
          return;
        }

        hideAll();
        var menu = getMenu(menuSelector);


        menu.settings.prepareMenu.call(this, menu, $(e.target));

        //open menu
        menu.$menu
          .data("invokedOn", $(e.target))
          .show()
          .css({
            position: "absolute",
            left: getMenuPosition(e.clientX, 'width', 'scrollLeft'),
            top: getMenuPosition(e.clientY, 'height', 'scrollTop')
          })
          .off('click')
          .on('click', 'a', function(e) {
            menu.$menu.hide();

            var $invokedOn = menu.$menu.data("invokedOn");
            var $selectedMenu = $(e.target);

            callOnMenuHide(menu);

            if ($selectedMenu.hasClass("checked")) {
              if ($selectedMenu.hasClass("dropdown-item-checked")) {
                $selectedMenu.removeClass("dropdown-item-checked");
              } else {
                $selectedMenu.addClass("dropdown-item-checked");
              }
            }

            if (!$selectedMenu.hasClass("disabled")) {
              menu.settings.menuSelected.call(this, $invokedOn, $selectedMenu);
            }
          });

        callOnMenuShow(menu);
        return false;
      });
    })($(this), settings);

    function getMenu(menuSelector) {
      var menu = null;
      $.each(menus, function(i_menuSelector, i_menu) {
        if (i_menuSelector == menuSelector) {
          menu = i_menu
          return false;
        }
      });
      return menu;
    }

    function hideAll() {
      $.each(menus, function(menuSelector, menu) {
        menu.$menu.hide();
        callOnMenuHide(menu);
      });
    }

    function callOnMenuShow(menu) {
      var $invokedOn = menu.$menu.data("invokedOn");
      if ($invokedOn && menu.settings.onMenuShow) {
        menu.settings.onMenuShow.call(this, $invokedOn);
      }
    }

    function callOnMenuHide(menu) {
      var $invokedOn = menu.$menu.data("invokedOn");
      menu.$menu.data("invokedOn", null);
      if ($invokedOn && menu.settings.onMenuHide) {
        menu.settings.onMenuHide.call(this, $invokedOn);
      }
    }

    function getMenuPosition(mouse, direction, scrollDir) {
      var win = $(window)[direction](),
        scroll = $(window)[scrollDir](),
        menu = $(settings.menuSelector)[direction](),
        position = mouse + scroll;

      // opening menu would pass the side of the page
      if (mouse + menu > win && menu < mouse) {
        position -= menu;
      }

      return position;
    }
    return this;
  };
})(jQuery, window);

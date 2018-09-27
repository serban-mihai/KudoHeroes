
define(["dojo/_base/declare", "dojo/_base/lang", "dojo/Deferred", "dojo/query", "dojo/on", "dojo/dom", "esri/request", "esri/urlUtils", "dijit/focus", "dijit/TooltipDialog", "dijit/popup"], function(
  declare, lang,
  Deferred, query, on,
  dom,
  esriRequest, urlUtils, focusUtil,
  TooltipDialog, popup) {
  return declare(null, {

    constructor: function(parameters) {
      var defaults = {
        config: {},
        title: window.document.title,
        summary: "",
        hashtags: "",
        image: "",
        map: null,
        url: window.location.href,
        bitlyAPI: location.protocol === "https:" ? "https://api-ssl.bitly.com/v3/shorten" : "http://api.bit.ly/v3/shorten",
        facebookURL: "https://www.facebook.com/sharer/sharer.php?s=100&p[url]={url}&p[images][0]={image}&p[title]={title}&p[summary]={summary}",
        twitterURL: "https://twitter.com/intent/tweet?url={url}&text={title}&hashtags={hashtags}"
      };

      lang.mixin(this, defaults, parameters);

      this.tooltipDialog = new TooltipDialog({
        id: "tooltip",
        tabIndex: 0
      });
      this.tooltipDialog.startup();


    },

    /* Public Methods */

    shareLink: function(clickNode, slideNum, type) {
      console.log(clickNode, slideNum, type);
      this._getUrl(slideNum).then(lang.hitch(this, function(response) {
        if (response) {
          var fullLink;
          var shareObj = {
            url: encodeURIComponent(response),
            title: encodeURIComponent(this.title),
            image: encodeURIComponent(this.image),
            summary: encodeURIComponent(this.summary),
            hashtags: encodeURIComponent(this.hashtags)
          };

          if (type === "facebook") {
            fullLink = lang.replace(this.facebookURL, shareObj);
            window.open(fullLink, "share", true);
          } else if (type === "twitter") {
            fullLink = lang.replace(this.twitterURL, shareObj);
            window.open(fullLink, "share", true);
          } else {
            fullLink = response;
            this.tooltipDialog.setContent("<input class='tip' type='text' value='" + fullLink + "' readonly/>");
            popup.open({
              popup: this.tooltipDialog,
              x: clickNode.pageX,
              y: clickNode.pageY
            });
            query(".tip").forEach(lang.hitch(this, function(node) {
              node.select();
              focusUtil.focus(node);
            }));
            on.once(this.tooltipDialog, "blur", lang.hitch(this, function() {
              popup.close(this.tooltipDialog);
            }));


          }
        }
      }));
    },

    /* Private Methods */

    //optional array of additional search layers to configure from the application config process
    _getUrl: function(slide) {
      var deferred = new Deferred();
      var urlObject = urlUtils.urlToObject(window.location.href);
      urlObject.query = urlObject.query || {};
      // Remove locale from url params
      if (urlObject.query.locale) {
        delete urlObject.query.locale;
      }
      // Add url params
      var url,
        useSeparator;
      if (slide) {
        urlObject.query.item = slide;
      }
      url = window.location.protocol + "//" + window.location.host + window.location.pathname;
      // append params
      for (var i in urlObject.query) {
        if (urlObject.query[i]) {
          // use separator
          if (useSeparator) {
            url += "&";
          } else {
            url += "?";
            useSeparator = true;
          }
          url += i + "=" + urlObject.query[i];
        }
      }
      if (this.config.bitlyKey && this.config.bitlyLogin) {
        // shorten the link
        var bitlyAPI = location.protocol === "https:" ? "https://api-ssl.bitly.com/v3/shorten" : "http://api.bit.ly/v3/shorten";
        esriRequest({
          url: bitlyAPI,
          callbackParamName: "callback",
          content: {
            uri: url,
            login: this.config.bitlyLogin,
            apiKey: this.config.bitlyKey,
            f: "json"
          },
          load: lang.hitch(this, function(response) {
            if (response && response.data && response.data.url) {
              deferred.resolve(response.data.url);
            } else {
              deferred.resolve(null);
            }
          }),
          error: function(error) {
            console.log(error);
            deferred.resolve(null);
          }
        });
      } else {
        deferred.resolve(url);
      }
      return deferred.promise;
    }

  });
});

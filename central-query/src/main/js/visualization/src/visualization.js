/**
 * visualization.js
 * create main visualization config object
 */
(function(){
    "use strict";

    // indicates if the base dependencies (stuff like
    // jquery, d3, etc) have been loaded
    var depsLoaded = false;

    /**
     * @memberOf zenoss
     * @namespace
     * @access public
     */
    visualization = {

        /**
         * Used to specify the base URL that is the endpoint for the Zenoss
         * metric service.
         *
         * @access public
         * @default http://localhost:8080
         */
        url: "http://localhost:8080",

        /**
         * The url path where the static javascript dependencies can be
         * found. This includes library dependencies like jquery.
         *
         * @access public
         * @default /static/performance/query
         */
        urlPath: "/static/performance/query/",

        /**
         * The url path where metrics are fetched from the server
         *
         * @access public
         * @default /api/performance/query
         */
        urlPerformance: "/api/performance/query/",


        /**
         * @namespace
         * @access public
         */
        chart : {
            /**
             * Looks up a chart instance by the given name and, if found,
             * updates the chart instance with the given changes. To remove
             * an item (at the first level or the change structure) set its
             * values to the negative '-' symbol.
             *
             * @param {string}
             *            name the name of the chart to update
             * @param {object}
             *            changes a configuration object that holds the
             *            changes to the chart
             */
            update : function(name, changes) {
                var found = getChart(name);
                if (found === undefined) {
                    debug.__warn('Attempt to modify a chart, "' + name +
                        '", that does not exist.');
                    return;
                }
                found.update(changes);
            },

            /**
             * Constructs a zenoss.visualization.Chart object, but first
             * dynamically loading any chart definition required, then
             * dynamically loading all dependencies, and finally creating
             * the chart object. This method should be used to create a
             * chart as opposed to calling "new" directly on the class.
             *
             * @param {string}
             *            name the name of the HTML div element to augment
             *            with the chart
             * @param {string}
             *            [template] the name of the chart template to load.
             *            The chart template will be looked up as a resource
             *            against the Zenoss metric service.
             * @param {object}
             *            [config] the values specified as the configuration
             *            will augment / override options loaded from any
             *            chart template that is specified, thus if no chart
             *            template is specified this configuration parameter
             *            can be used to specify the entire chart
             *            definition.
             * @param {callback}
             *            [success] this callback will be called when a
             *            zenoss.visualization.Chart object is successfully
             *            created. The reference to the Chart object will be
             *            passed as a parameter to the callback.
             * @param {callback}
             *            [fail] this callback will be called when an error
             *            is encountered during the creation of the chart.
             *            The error that occurred will be passed as a
             *            parameter to the callback.
             */
            create : function(name, config) {

                var chart,
                    deferred = utils.getDeferred();

                if (!depsLoaded) {
                    dependency.__bootstrap(function() {
                        depsLoaded = true;
                        chart = new Chart(name, config);
                        cacheChart(chart);
                        deferred.resolve(chart);
                    });
                } else {
                    chart = new Chart(name, config);
                    cacheChart(chart);
                    deferred.resolve(chart);
                }

                return deferred.promise();
            },

            // expose chart cache getter
            getChart: getChart
        }
    };

    var projectionAlgorithms = {

        /**
         * Uses the supplied values to return a function that accepts
         * an "x" value and returns a "y" value.
         * Library: "regression.js" https://github.com/Tom-Alexander/regression-js
         **/
        linear: function(projection, xValues, yValues) {
            // the regression library an array of x,y values, e.g.  [[x, y], [x1, y1],...]
            var data = [], i, formula, slope, intercept;
            for (i=0;i < xValues.length; i++) {
                data.push([xValues[i], yValues[i]]);
            }
            // return basically an empty function so clients do not error out when calling it
            if (data.length === 0) {
                return function(x){ return 0; };
            }

            // linear regression
            formula = window.regression("linear", data);
            slope = formula.equation[0];
            intercept = formula.equation[1];

            return function(x) {
                // y = mx + b
                return (slope * x) + intercept;
            };
        },
        polynomial: function(projection, xValues, yValues) {
            // the regression library an array of x,y values, e.g.  [[x, y], [x1, y1],...]
            var data = [], i, formula, slopes, intercept, n;
            for (i=0;i < xValues.length; i++) {
                data.push([xValues[i], yValues[i]]);
            }
            // return basically an empty function so clients do not error out when calling it
            if (data.length === 0) {
                return function(x){ return 0; };
            }

            // polynomial regression
            n = parseInt(projection.parameters['n'] || 2);
            formula = window.regression("polynomial", data, n);

            // first entry is the intercept
            intercept = formula.equation.shift();

            slopes = formula.equation;
            // work with the largest number first
            slopes.reverse();
            //equation is something like: "y = 0.1x^2 + 0.2x + -64010.58"
            // where the coeffecients are in the array "slopes"
            return function(x) {
                var currentPow = n, i, result = 0;
                // all the cx^y parts
                for (i=0; i < slopes.length; i++) {
                    result += slopes[i] * (Math.pow(x, currentPow));
                    currentPow--;
                }
                // finally add the intercept
                result += intercept;
                return result;
            };
        }
    };
    visualization.projections = projectionAlgorithms;

    // chart cache with getter/setters
    var chartCache = {};

    function cacheChart(chart){
        chartCache[chart.name] = chart;

        // automatically remove this chart
        // if the containing dom element
        // is destroyed
        chart.onDestroyed = function(e){
            removeChart(chart.name);
        };

        // TODO - watch for stale charts
    }

    function removeChart(name){
        delete chartCache[name];
    }

    function getChart(name){
        return chartCache[name];
    }

})();

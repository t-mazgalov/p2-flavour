angular
    .module('AppP2F', ['ngMaterial', 'ngRoute', 'ngAnimate', 'ngVis'])
    .config(function($mdThemingProvider) {
      $mdThemingProvider
          .theme('default')
          .primaryPalette('teal')
          .accentPalette('blue');
    })
    .config(function($routeProvider) {
      $routeProvider
        .when('/home', {
          templateUrl: 'jsp/home.jsp'
        })
        .when('/profiles', {
            templateUrl: 'jsp/profiles.jsp'
        })
        .when('/repositories', {
            templateUrl: 'jsp/repositories.jsp'
        })
        .when('/provisioning-lists', {
            templateUrl: 'jsp/provisioning-lists.jsp'
        })
        .when('/provision', {
            templateUrl: 'jsp/provisioning.jsp'
        })
    })
    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push(['$rootScope', '$q', '$injector', function($rootScope, $q, $injector) {
            var toaster;

            function getToaster() {
              if (!toaster) {
                toaster = $injector.get("$mdToast");
              }
              return toaster;
            }

            return {
                'responseError': function(response) {
                    getToaster().show(
                      getToaster().simple()
                        .textContent("ERROR: " + response.data)
                        .toastClass('md-warn')
                        .position('bottom right')
                        .hideDelay(3000)
                    );

                    return $q.reject(response);
                }
            };
        }]);
    }])
    .controller('AppCtrl', function (
                                $scope,
                                $timeout,
                                $mdSidenav,
                                $route,
                                $routeParams,
                                $location
                                ) {
        $scope.toggleLeft = buildToggler('left');

        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;

        function buildToggler(componentId) {
          return function() {
            $mdSidenav(componentId).toggle();
          };
        };
    });
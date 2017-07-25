angular
    .module('AppP2F', ['ngMaterial', 'ngRoute'])
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
    })
    .controller('AppCtrl', function ($scope, $timeout, $mdSidenav, $route, $routeParams, $location) {
        $scope.toggleLeft = buildToggler('left');

        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;

        function buildToggler(componentId) {
          return function() {
            $mdSidenav(componentId).toggle();
          };
        }
    })
;
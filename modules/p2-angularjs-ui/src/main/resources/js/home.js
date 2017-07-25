angular
    .module('AppP2F')
    .controller('IUsListCtrl', function($scope, $http, $mdDialog) {
        $http
            .get("/p2f/ius")
            .then(function(response) {
                $scope.ius = response.data;
            });
    });
angular
    .module('AppP2F')
    .controller('ProvisioningController', function($scope, $http) {
        $scope.loadProvLists = function() {
            $http
                .get("/rs/provisioning-lists/list")
                .then(function(response) {
                    $scope.provLists = response.data;
                })
        }

        $scope.loadProfiles = function() {
            $http
                .get("/rs/profiles/list")
                .then(function(response) {
                    $scope.profiles = response.data;
                });
        }


        $scope.provisionProvList = function() {
            var params = {
                'profile': $scope.selectedProfile,
                'simplifiedProvisioningList': $scope.selectedProvList
            };

            $http
                .post("/rs/provisioning-lists/provision", params)
                .then(function(response) {
                    console.log(response.data);
                });
        }
    });
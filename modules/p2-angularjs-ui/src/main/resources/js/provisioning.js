angular
    .module('AppP2F')
    .controller('ProvisioningController', function($scope, $http) {
        $scope.disabledProgressProvision = true;

        $scope.loadProvLists = function() {
            return $http
                .get("/rs/provisioning-lists/list")
                .then(function(response) {
                    $scope.provLists = response.data;
                })
        }

        $scope.loadProfiles = function() {
           return  $http
                .get("/rs/profiles/list")
                .then(function(response) {
                    $scope.profiles = response.data;
                });
        }


        $scope.provisionProvList = function() {
            $scope.disabledProgressProvision = false;

            var params = {
                'profile': $scope.selectedProfile,
                'simplifiedProvisioningList': $scope.selectedProvList
            };

            $http
                .post("/rs/provisioning-lists/provision", params)
                .then(function(response) {
                    $scope.provisioningOutput = response.data;
                    $scope.disabledProgressProvision = true;
                });
        }
    });
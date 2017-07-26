angular
    .module('AppP2F')
    .controller('ProfilesController', function($scope, $http) {
        $scope.currentProfile
        function checkCurrentProfile() {
            $http
                .get("/rs/profiles/current")
                .then(function(response) {
                    $scope.currentProfile = response.data;
                });
        }
        checkCurrentProfile();

        $scope.loadProfiles = function() {
            return $http
                .get("/rs/profiles/list")
                .then(function(response) {
                    $scope.profiles = response.data;
                });
        }

        $scope.createProfile = function() {
            var params = {
                'name': $scope.profileName,
                'location': $scope.profileLocation
            };

            $http
                .post("/rs/profiles/create", params)
                .then(function(response) {
                    $scope.profiles = response.data;
                });
        }

        $scope.changeProfile = function() {
            $http
                .put("/rs/profiles/change/" + $scope.selectedProfile.id)
                .then(function(response) {
                    checkCurrentProfile();
                });
        }
    });
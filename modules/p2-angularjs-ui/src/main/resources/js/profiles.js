angular
    .module('AppP2F')
    .controller('ProfilesController', function($scope, $http) {
        $scope.currentProfile

        $scope.checkCurrentProfile = function() {
            getCurrentProfile();
        }

        function getCurrentProfile() {
            $http
                .get("/rs/profiles/current")
                .then(function(response) {
                    $scope.currentProfile = response.data;
                    $scope.disabledProgressChangeProfiles = true;
                });
        }

        $scope.loadProfiles = function() {
            $scope.disabledProgressListProfiles = false;
            return $http
                .get("/rs/profiles/list")
                .then(function(response) {
                    $scope.profiles = response.data;
                    $scope.disabledProgressListProfiles = true;
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
                    $scope.loadProfiles();
                });
        }

        $scope.changeProfile = function() {
            $scope.disabledProgressChangeProfiles = false;
            $http
                .put("/rs/profiles/change/" + $scope.selectedProfile.id)
                .then(function(response) {
                    getCurrentProfile();
                });
        }
    });
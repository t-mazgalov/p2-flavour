angular
    .module('AppP2F')
    .controller('RepositoriesController', function($scope, $http, $mdDialog) {
        function getRepos() {
            $http
                .get("/rs/repos/list")
                .then(function(response) {
                    $scope.repos = response.data;
                })
        }

        function getProvisioningLists() {
            $http
                .get("/rs/provisioning-lists/list")
                .then(function(response) {
                    $scope.provLists = response.data;
                })
        }

        function setCurrentProvList(provList) {
            $scope.currentProvList = provList;
        }

        getRepos() // Initial call
        getProvisioningLists() // Initial call

        $scope.listMetadataRepo = function(profileId, repoLocation) {
            $scope.loadedRepoCard = true;

            $http
                .get("/rs/repos/list/" + profileId + "/metadata/" + repoLocation)
                .then(function(response) {
                    var responseData = response.data;
                    for(i in responseData) {
                        if(responseData[i].artifacts.length > 0) {
                            var classifier = responseData[i].artifacts[0].classifier;
                            if(classifier === 'org.eclipse.update.feature') {
                                responseData[i].iuType = 'feature'
                            } else if(classifier === 'osgi.bundle') {
                                responseData[i].iuType = 'bundle'
                            } else {
                                responseData[i].iuType = 'installable-unit'
                            }
                        } else {
                            responseData[i].iuType = 'installable-unit'
                        }
                    }
                    $scope.ius = responseData;
                    $scope.listedRepoLocation = repoLocation
                    getRepos(); // Update repos
                });
        }

        $scope.listArtifactsRepo = function(profileId, repoLocation) {
            $scope.loadedRepoCard = true;

            $http
                .get("/rs/repos/list/" + profileId + "/artifacts/" + repoLocation)
                .then(function(response) {
                    var responseData = response.data;
                    for(i in responseData) {
                        responseData[i].iuType = 'artifact-key'
                    }
                    $scope.ius = responseData;
                    $scope.listedRepoLocation = repoLocation
                    getRepos(); // Update repos
                });
        }

        $scope.loadRepo = function() {
            $scope.loadedRepoCard = true;
            $http
                .get("/rs/profiles/current")
                .then(function(response) {
                    var systemProfile = response.data;
                    var params = {
                        'profileName': systemProfile.name,
                        'profileLocation': systemProfile.location,
                        'metadataLocation': $scope.metadataLocation,
                        'artifactsLocation': $scope.artifactsLocation
                    };

                    $http
                        .post("/rs/repos/load", params)
                        .then(function(loadResponse) {
                            $scope.ius = loadResponse.data;
                            $scope.listedRepoLocation = $scope.metadataLocation;
                            getRepos(); // Update repos
                        });

                });
        }

        $scope.removeRepo = function(repoType, repoLocation) {
            $http
                .delete("/rs/repos/remove/" + repoType + "/" + repoLocation)
                .then(
                    function(response) {
                        getRepos();
                    }
                )
        }

        $scope.loadProfiles = function() {
            $http
                .get("/rs/profiles/list")
                .then(function(response) {
                    $scope.profiles = response.data;
                });
        }

        $scope.addProvListIUs = function(provList,iu,listedRepoLocation) {
            var params = {
                'id': provList.id,
                'installableUnits': [{
                    'id': iu.id,
                    'type': iu.iuType,
                    'repository': listedRepoLocation,
                    'major': iu.version.major,
                    'minor': iu.version.minor,
                    'micro': iu.version.micro,
                    'qualifier': iu.version.qualifier
                }]
            };

            $http
                .post("/rs/provisioning-lists/addIUs", params)
                .then(function(response) {
                    $http
                        .get("/rs/provisioning-lists/" + response.data.id)
                        .then(function(response) {
                            setCurrentProvList(response.data);
                        })
                });
        }

        $scope.removeProvListIUs = function(provList,iu) {
            var params = {
                'id': provList.id,
                'installableUnits': [{
                    'id': iu.id,
                    'type': iu.type,
                    'major': iu.major,
                    'minor': iu.minor,
                    'micro': iu.micro,
                    'qualifier': iu.qualifier
                }]
            };

            $http
                .post("/rs/provisioning-lists/removeIUs", params)
                .then(function(response) {
                    $http
                        .get("/rs/provisioning-lists/" + response.data.id)
                        .then(function(response) {
                            setCurrentProvList(response.data);
                        })
                });
        }

        $scope.showCreateListDialog = function(event, iu, listedRepoLocation) {
            var confirm = $mdDialog.prompt()
                .title('Create new installable units list')
                .placeholder('List name')
                .ariaLabel('List name')
                .targetEvent(event)
                .ok('Create')
                .cancel('Cancel');

            $mdDialog.show(confirm).then(
                function(name) {
                    var params = {
                        'name': name,
                        'installableUnits': [{
                            'id': iu.id,
                            'type': iu.iuType,
                            'repository': listedRepoLocation,
                            'major': iu.version.major,
                            'minor': iu.version.minor,
                            'micro': iu.version.micro,
                            'qualifier': iu.version.qualifier
                        }]
                    };

                    $http
                        .post("/rs/provisioning-lists/create", params)
                        .then(function(response) {
                            getProvisioningLists();
                            $http
                                .get("/rs/provisioning-lists/" + response.data.id)
                                .then(function(response) {
                                    setCurrentProvList(response.data);
                                })
                        });
                }, function() {

                }
            );
        };
    });
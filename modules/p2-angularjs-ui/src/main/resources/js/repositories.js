angular
    .module('AppP2F')
    .controller('RepositoriesController', function($scope, $http, $mdDialog, $mdToast) {
        $scope.iusGraphStabilizationIterationsDone = function() {
            $scope.iusGraphOptions.physics = false;
            $scope.$digest();
        }
        $scope.$watchCollection('iusGraphOptions.nodes', function (nodes) {
            $scope.iusGraphOptions.physics = true;
        });
        $scope.iusGraphOptions = {
            autoResize: true,
            height: '500',
            width: '100%',
            nodes: {
                font: {color:'#000000'},
                "shadow": false
            },
            physics: {
                forceAtlas2Based: {
                    gravitationalConstant: -26,
                    centralGravity: 0.005,
                    springLength: 230,
                    springConstant: 0.18,
                    avoidOverlap: 1.5
                },
                maxVelocity: 146,
                solver: 'forceAtlas2Based',
                timestep: 0.35,
                stabilization: {
                    enabled: true,
                    iterations: 1000,
                    updateInterval: 25
                }
            }
        };
        $scope.iusGraphEvents = {
            stabilizationIterationsDone: $scope.iusGraphStabilizationIterationsDone
        };

        $scope.checkCurrentProfile = function () {
            return $http
                .get("/rs/profiles/current")
                .then(function(response) {
                    $scope.currentProfile = response.data;
                });
        }

        $scope.filterRepos = function (repo) {

            if($scope.filerProfileRepos == null) {
                return repo.profile.name === $scope.currentProfile.name;
            }
            return repo.profile.name === $scope.filerProfileRepos;
        };

        function getRepos() {
            $scope.disabledProgressListRepos = false;

            $http
                .get("/rs/repos/list")
                .then(function(response) {
                    $scope.repos = response.data;
                    $scope.disabledProgressListRepos = true;
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
            $scope.disabledProgressManageProvList = true;
        }

        getRepos(); // Initial call
        getProvisioningLists(); // Initial call

        $scope.listMetadataRepo = function(profileId, repoLocation) {
            $scope.loadedRepoCard = true;
            $scope.disabledProgressListRepo = false;

            $http
                .get("/rs/repos/list/" + profileId + "/metadata/" + repoLocation)
                .then(function(response) {
                    var responseData = response.data;
                    for(i in responseData) {
                        if(responseData[i].id.endsWith('.feature.group')) {
                            responseData[i].iuType = 'feature';
                            continue;
                        }
                        if(responseData[i].artifacts.length > 0) {
                            var classifier = responseData[i].artifacts[0].classifier;
                            if(classifier === 'osgi.bundle') {
                                responseData[i].iuType = 'bundle';
                                continue;
                            }
                        }
                        responseData[i].iuType = 'installable-unit';
                    }
                    $scope.ius = responseData;
                    $scope.listedRepoLocation = repoLocation;
                    $scope.selectedProfileId = profileId;
                    getRepos(); // Update repos
                    $scope.disabledProgressListRepo = true;
                });
        }

        $scope.listArtifactsRepo = function(profileId, repoLocation) {
            $scope.loadedRepoCard = true;
            $scope.disabledProgressListRepo = false;

            $http
                .get("/rs/repos/list/" + profileId + "/artifacts/" + repoLocation)
                .then(function(response) {
                    var responseData = response.data;
                    for(i in responseData) {
                        responseData[i].iuType = 'artifact-key'
                    }
                    $scope.ius = responseData;
                    $scope.listedRepoLocation = repoLocation;
                    getRepos(); // Update repos
                    $scope.disabledProgressListRepo = true;
                });
        }

        $scope.loadRepo = function() {
            $scope.disabledProgressListRepo = false;

            $http
                .get("/rs/profiles/current")
                .then(function(response) {
                    var systemProfile = response.data;
                    var params = {
                        'profileName': $scope.selectedProfile.name,
                        'profileLocation': $scope.selectedProfile.location,
                        'metadataLocation': $scope.metadataLocation,
                        'artifactsLocation': $scope.artifactsLocation
                    };

                    $http
                        .post("/rs/repos/load", params)
                        .then(function(loadResponse) {
                            getRepos(); // Update repos
                            $scope.disabledProgressListRepo = true;
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
            return $http
                .get("/rs/profiles/list")
                .then(function(response) {
                    $scope.profiles = response.data;
                });
        }

        $scope.addProvListIUs = function(provList,iu,listedRepoLocation) {
            $scope.disabledProgressManageProvList = false;

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
            $scope.disabledProgressManageProvList = false;

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
                    $scope.disabledProgressManageProvList = false;

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
                                });
                        });
                }, function() {

                }
            );
        };

        $scope.loadIusGraph = function(profileId, repoLocation) {
            $scope.disabledProgressIusGraph = false;

            return $http
                .get("/rs/repos/graph/" + profileId + "/metadata/" + repoLocation)
                .then(function(response) {
                    var responseData = response.data;

                    var iusGraphDataNodes = responseData.graphInstallableUnits;
                    var iusGraphDataEdges = responseData.graphInstallableUnitRequirements;

                    var provisioningListsDacData = {
                        "nodes": iusGraphDataNodes,
                        "edges": iusGraphDataEdges
                    }

                    $scope.iusGraphOptions.physics = true;
                    $scope.iusGraphData = provisioningListsDacData;

                    $scope.disabledProgressIusGraph = true;
                });
        };

        $scope.loadIuGraph = function(profileId, repoLocation, iuId, iuVersion) {
            $scope.disabledProgressIusGraph = false;

            var version = iuVersion.major + "." + iuVersion.minor + "." + iuVersion.micro + "." + iuVersion.qualifier
            return $http
                .get("/rs/repos/graph/" + profileId + "/iu/" + iuId + "/" + version + "/" + repoLocation)
                .then(function(response) {
                    var responseData = response.data;

                    var iusGraphDataNodes = responseData.graphInstallableUnits;
                    var iusGraphDataEdges = responseData.graphInstallableUnitRequirements;

                    var provisioningListsDacData = {
                        "nodes": iusGraphDataNodes,
                        "edges": iusGraphDataEdges
                    }

                    $scope.iusGraphOptions.physics = true;
                    $scope.iusGraphData = provisioningListsDacData;

                    $scope.disabledProgressIusGraph = true;
                });
        };
    });
angular
    .module('AppP2F')
    .controller('ProvisioningListsController', function($scope, $http, VisDataSet) {
        $scope.showSystemProvList = false;
        $scope.data = {}
        $scope.options = {
            autoResize: true,
            height: '500',
            width: '100%',
            nodes : {
                font:{color:'#ffffff'},
                "color": "#009688",
                "shape":"circle",
                "shadow":false
            }
        };

        $scope.loadProvLists = function() {
            $scope.disabledProgressListProvLists = false;
            $scope.disabledProgressGenerateDAG = false;

            return $http
                .get("/rs/provisioning-lists/list")
                .then(function(response) {
                    var responseData = response.data;
                    $scope.provLists = responseData;

                    var provisioningListsDacDataNodes = [];
                    var provisioningListsDacDataEdges = [];
                    for(var i in responseData) {
                        if($scope.showSystemProvList === false && responseData[i].name === "system")
                            continue;
                        var provisioningListsDacDataNode = {
                            "id" : responseData[i].id,
                            "label": responseData[i].name,
                            "value": responseData[i].installableUnits.length
                        }
                        provisioningListsDacDataNodes.push(provisioningListsDacDataNode);

                        for(var extendId in responseData[i].extendedListIds) {
                            var provisioningListsDacDataEdge = {
                                "from": responseData[i].id,
                                "to": responseData[i].extendedListIds[extendId],
                                arrows:'to'
                            }
                            provisioningListsDacDataEdges.push(provisioningListsDacDataEdge);
                        }
                    }

                    var provisioningListsDacData = {
                        "nodes": provisioningListsDacDataNodes,
                        "edges": provisioningListsDacDataEdges
                    }

                    console.log(provisioningListsDacData);

                    $scope.data = provisioningListsDacData;

                    $scope.disabledProgressListProvLists = true;
                    $scope.disabledProgressGenerateDAG = true;
                })
        }

        $scope.extendProvList = function(fromList, toList) {
            return $http
                .put("/rs/provisioning-lists/extend/" + fromList.id + "/" + toList.id)
                .then(function(response) {
                    $scope.loadProvLists();
                })
        }

        $scope.shrinkProvList = function(fromList, toList) {
            return $http
                .put("/rs/provisioning-lists/shrink/" + fromList.id + "/" + toList.id)
                .then(function(response) {
                    $scope.loadProvLists();
                })
        }


    });
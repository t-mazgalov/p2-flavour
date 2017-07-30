<!DOCTYPE html>
<html>
<head>
    <title>P2 Flavours</title>
    <link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/angular_material/1.1.0/angular-material.min.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="css/p2fApp.css">
    <link rel="stylesheet" href="css/animations.css">
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular-animate.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular-aria.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular-messages.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.5.5/angular-route.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/angular_material/1.1.0/angular-material.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/vis/4.3.0/vis.js"></script>
    <script src="js/angular-vis.js"></script>
    <script src="js/p2fApp.js"></script>
    <script src="js/home.js"></script>
    <script src="js/repositories.js"></script>
    <script src="js/profiles.js"></script>
    <script src="js/provisioning.js"></script>
    <script src="js/provisioning-lists.js"></script>
</head>
<body ng-app="AppP2F" ng-cloak>
    <div ng-controller="AppCtrl" layout-fill layout="column" ng-cloak>

      <section layout="column" flex>

        <md-toolbar layout="row">
            <md-button ng-click="toggleLeft()">
                <md-icon class="material-icons md-light">menu</md-icon>
            </md-button>

            <div layout="row" layout-align="center center">
                <img ng-src="img/logo.png"/>
            </div>
        </md-toolbar>



        <md-content flex>
            <md-sidenav class="md-sidenav-left" md-component-id="left" md-whiteframe="4">
              <md-content>
                <md-button ng-href="#home?name=Dashboard" layout-padding layout="row" class="nav-link">
                    <span>
                        <md-icon class="material-icons">dashboard</md-icon>
                        <span class="nav-link-title">Dashboard</span>
                    </span>
                </md-button>
                <md-button ng-href="#profiles?name=Profiles" layout-padding layout="row" class="nav-link">
                    <span>
                        <md-icon class="material-icons">settings</md-icon>
                        <span class="nav-link-title">Profiles</span>
                    </span>
                </md-button>
                <md-button ng-href="#repositories?name=Repositories" layout-padding layout="row" class="nav-link">
                    <span>
                        <md-icon class="material-icons">input</md-icon>
                        <span class="nav-link-title">Repositories</span>
                    </span>
                </md-button>
                <md-button ng-href="#provisioning-lists?name=Provisioning Lists" layout-padding layout="row" class="nav-link">
                    <span>
                        <md-icon class="material-icons">list</md-icon>
                        <span class="nav-link-title">Provisioning Lists</span>
                    </span>
                </md-button>
                <md-button ng-href="#provision?name=Provision" layout-padding layout="row" class="nav-link">
                    <span>
                        <md-icon class="material-icons">file_download</md-icon>
                        <span class="nav-link-title">Provision</span>
                    </span>
                </md-button>
              </md-content>
            </md-sidenav>
            <md-content>
                <div ng-view></div>
            </md-content>
        </md-content>

      </section>
    </div>
</body>
</html>